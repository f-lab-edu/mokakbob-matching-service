import ws from 'k6/ws';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';

const sendLatency = new Trend('chat_send_latency', true);
const recvLatency = new Trend('chat_receive_latency', true);
const messageLoss = new Counter('chat_message_loss');
const connectionError = new Counter('chat_connection_error');

const payload = "x".repeat(1024);

const DEFAULT_MESSAGE_INTERVAL = 1;
const MESSAGE_INTERVAL = Number(__ENV.MESSAGE_INTERVAL || DEFAULT_MESSAGE_INTERVAL);

export const options = {
    vus: 100,
    duration: '10m',
};

const token = __ENV.TOKEN || 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzYzMDc5MzIyLCJleHAiOjE3NjMwODI5MjJ9.psMbc8nvgTo6BwOEBOsaakiAg-altMn5h4uVx6i3tTk';
const mode = __ENV.MODE || 'pubsub'; // pubsub or cluster
const ports = [8080, 8081, 8082];
const baseHost = __ENV.HOST || 'localhost';

export default function () {
    const vu = __VU;
    const roomId = vu % 10;
    const port = ports[vu % ports.length];
    const WS_URL = `ws://${baseHost}:${port}/ws`;

    let seq = 0; // 메시지 시퀀스 번호

    const res = ws.connect(
        WS_URL,
        { headers: { Authorization: token } },
        function (socket) {
            let connected = false;
            let subscribed = false;

            let lastSendSeq = -1;
            let lastSendTimestamp = 0;
            let awaitingAck = false;

            socket.on('open', () => {
                socket.send(
                    `CONNECT\naccept-version:1.2\nheart-beat:10000,10000\nAuthorization:${token}\n\n\x00`
                );
            });

            socket.on('message', (raw) => {
                // CONNECTED 처리
                if (raw.includes('CONNECTED') && !connected) {
                    connected = true;
                    socket.send(
                        `SUBSCRIBE\nid:sub-${roomId}\ndestination:/sub/chat/${roomId}\n\n\x00`
                    );

                    socket.setTimeout(() => {
                        subscribed = true;
                    }, 300);

                    return;
                }

                if (!subscribed) return;

                // MESSAGE 프레임 처리
                if (raw.includes('MESSAGE')) {
                    const body = raw.split('\n\n')[1]?.replace(/\x00$/, '');
                    try {
                        const msg = JSON.parse(body);
                        if (msg.seq === lastSendSeq) {
                            const now = Date.now();
                            recvLatency.add(now - lastSendTimestamp);
                            awaitingAck = false;
                        }
                    } catch (e) {}
                }
            });

            // 주기적 메시지 전송
            socket.setInterval(() => {
                if (!subscribed) return;

                if (awaitingAck) {
                    // 지난 메시지를 못 받음 > 유실로 처리
                    messageLoss.add(1);
                }

                const seqId = seq++;
                lastSendSeq = seqId;
                lastSendTimestamp = Date.now();
                awaitingAck = true;

                const body = JSON.stringify({
                    content: `${mode}-Hello-${vu}`,
                    seq: seqId,
                    payload,
                });

                const start = Date.now();
                socket.send(
                    `SEND\ndestination:/pub/chat/${roomId}\ncontent-type:application/json\n\n${body}\x00`
                );
                const end = Date.now();

                sendLatency.add(end - start);
            }, MESSAGE_INTERVAL * 1000);

            socket.on('error', () => {
                connectionError.add(1);
            });
        }
    );

    check(res, { 'connected successfully': (r) => r && r.status === 101 });
}
