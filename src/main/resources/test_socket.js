const { Client } = require('@stomp/stompjs');
const SockJS = require('sockjs-client');

// Token JWT
const token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXb3JkV2lzZSIsImNoYW5uZWwiOiJhZG1pbi9hNjI4ZTc2NC0yZDc2LTQ5NjYtYTZjOC1mODJiYjczYzVhOWQiLCJleHAiOjE3NTc0MzQ5ODgsImlhdCI6MTc1NzQzMTM4OCwidXNlcklkIjoiNWRkNzQwOWYtMjdlYi00ZTVjLTgwZjMtYzU0NmY1NTMzYjhjIn0.y2Xf7_t5QKaoBdXLSN6aGKqsgYchjjDHWOpxgePxBhI"
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

    client.subscribe('/topic/admin/a628e764-2d76-4966-a6c8-f82bb73c5a9d', (message) => {
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
