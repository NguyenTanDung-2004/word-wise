const { Client } = require('@stomp/stompjs');
const SockJS = require('sockjs-client');

// Token JWT
const token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXb3JkV2lzZSIsImNoYW5uZWwiOiJwZXJzb25hbC9iMTI4YjgyOC0xZTkwLTQ4YjgtOWNjOC0yZDAzMDEzZWIwOTIiLCJleHAiOjE3NTc3NDk5NTksImlhdCI6MTc1Nzc0NjM1OSwidXNlcklkIjoiODExZDkxNmQtNzZkNS00Mzk2LTg1YjUtODdmNjg1MTc0ZjNlIn0.qnv31y5Iem669_r7dfETiYCJAER22d61byfYl6Xokho"
// Backend endpoint
const socketUrl = 'http://localhost:8080/ws?token=' + token;

const client = new Client({
  webSocketFactory: () => new SockJS(socketUrl),
  connectHeaders: {
    Authorization: `Bearer ${token}`
  },
  debug: function (str) {
    console.log(str);
  },
  onConnect: (frame) => {
    console.log('✅ Connected: ' + frame);

    client.subscribe('/topic/request/b128b828-1e90-48b8-9cc8-2d03013eb092', (message) => {
      console.log("📩 Received: " + message.body);
    });

    client.publish({
      destination: "/app/sendMessage",
      body: JSON.stringify({
        type: "admin",
        roomId: "b128b828-1e90-48b8-9cc8-2d03013eb092",
        message: "Hello from client!"
      }),
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  },
  onStompError: (frame) => {
    console.error('❌ Broker error: ' + frame.headers['message']);
    console.error('Details: ' + frame.body);
  },
});

client.activate();
