import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

const sendLatency = new Trend('chat_send_latency', true);
const recvLatency = new Trend('chat_receive_latency', true);
const messageLoss = new Counter('chat_message_loss');
const connectionError = new Counter('chat_connection_error');

export const options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '3m', target: 100 },
        { duration: '1m', target: 0 },
    ],
    thresholds: {
        checks: ['rate>0.95'],
        chat_send_latency: ['p(95)<500'],
    },
};

const token =
    __ENV.TOKEN ||
    'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzYzMDE5NTAzLCJleHAiOjE3NjMwMjMxMDN9.ar3yM6w6SptL6ebTbPlHVm7dfDM56jBJNIMZI6VhftA';
const mode = __ENV.MODE || 'pubsub'; // pubsub | cluster
const ports = [8080, 8081, 8082];
const baseHost = __ENV.HOST || 'localhost';

export default function () {
    const vu = __VU;
    const roomId = vu % 10;
    const port = ports[vu % ports.length];
    const WS_URL = `ws://${baseHost}:${port}/ws`;

    const connectStart = Date.now();

    const res = ws.connect(
        WS_URL,
        { headers: { Authorization: token } },
        function (socket) {
            let connected = false;
            let subscribed = false;
            let messageReceived = false;
            let sendTime = 0;

            socket.on('open', () => {
                socket.send(
                    `CONNECT\naccept-version:1.2\nheart-beat:10000,10000\nAuthorization:${token}\n\n\x00`
                );
            });

            socket.on('message', (data) => {
                // CONNECTED 수신 → SUBSCRIBE
                if (data.includes('CONNECTED')) {
                    connected = true;
                    socket.send(`SUBSCRIBE\nid:sub-${roomId}\ndestination:/sub/chat/${roomId}\n\n\x00`);
                }

                // SUBSCRIBE 이후 → 메시지 발송
                if (data.includes('RECEIPT') || (connected && !subscribed)) {
                    subscribed = true;
                    sendTime = Date.now();
                    socket.send(
                        `SEND\ndestination:/pub/chat/${roomId}\ncontent-type:application/json\n\n{"content":"${mode}-Hello ${vu}"}\x00`
                    );
                    sendLatency.add(Date.now() - connectStart); // 연결 시작 > 전송까지의 지연
                }

                // MESSAGE 수신 시 → 수신 지연 기록
                if (data.includes('MESSAGE') && data.includes(`${mode}-Hello ${vu}`)) {
                    recvLatency.add(Date.now() - sendTime); // 전송 > 수신까지의 지연
                    messageReceived = true;
                    socket.close();
                }
            });

            socket.on('close', () => {
                // 수신 실패한 경우 메시지 손실로 간주
                if (!messageReceived) {
                    messageLoss.add(1);
                }
            });

            socket.on('error', () => {
                connectionError.add(1);
            });

            socket.setTimeout(() => {
                if (!messageReceived) {
                    socket.close();
                }
            }, 10000); // 10초로 증가
        }
    );

    // 연결 성공 여부 확인
    const ok = check(res, {
        'connected successfully': (r) => r && r.status === 101,
    });
    if (!ok) connectionError.add(1);

    sleep(1);
}
