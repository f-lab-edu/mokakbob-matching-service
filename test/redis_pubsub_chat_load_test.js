import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

const sendLatency = new Trend('chat_send_latency', true);
const recvLatency = new Trend('chat_receive_latency', true);
const messageLoss = new Counter('chat_message_loss');

export const options = {
    stages: [
        { duration: '10s', target: 50 },
        { duration: '20s', target: 100 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        checks: ['rate>0.95'],
        chat_send_latency: ['p(95)<500'],
    },
};

const token = __ENV.TOKEN || 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzYyODM4ODAxLCJleHAiOjE3NjI4NDI0MDF9.qZn3-Kb9P-ZEK-j8XLggLAf9OCCUFPHgDA5b0fvNY_Y';
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
            let messageReceived = false;
            const startTime = Date.now();

            socket.on('open', () => {
                socket.send(
                    `CONNECT\naccept-version:1.2\nheart-beat:10000,10000\nAuthorization:${token}\n\n\x00`
                );
            });

            socket.on('message', (data) => {
                if (data.includes('CONNECTED')) {
                    connected = true;
                    socket.send(`SUBSCRIBE\nid:sub-${roomId}\ndestination:/sub/chat/${roomId}\n\n\x00`);
                    socket.send(
                        `SEND\ndestination:/pub/chat/${roomId}\ncontent-type:application/json\n\n{"content":"${mode}-Hello ${vu}"}\x00`
                    );
                    sendLatency.add(Date.now() - connectStart);
                }

                if (data.includes('MESSAGE') && data.includes(`${mode}-Hello ${vu}`)) {
                    recvLatency.add(Date.now() - startTime);
                    messageReceived = true;
                    socket.close();
                }
            });

            socket.on('close', () => {
                if (!messageReceived) {
                    messageLoss.add(1);
                }
            });

            socket.setTimeout(() => socket.close(), 5000);
        }
    );
    check(res, {
        'connected successfully': (r) => r && r.status === 101,
    });

    sleep(1);
}
