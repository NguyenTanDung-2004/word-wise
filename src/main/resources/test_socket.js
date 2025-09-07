const { Client } = require('@stomp/stompjs');
const SockJS = require('sockjs-client');

// Token JWT
const token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXb3JkV2lzZSIsImNoYW5uZWwiOiJhZG1pbi85MzM3ZDlhNy1kNzc4LTRkMTktYjBiNy1iMzE0MTZiYjU0YzEiLCJleHAiOjE3NTcyNTg1MzgsImlhdCI6MTc1NzI1NDkzOCwidXNlcklkIjoiNWRkNzQwOWYtMjdlYi00ZTVjLTgwZjMtYzU0NmY1NTMzYjhjIn0.wpml1wmrDWs7GjTUkBC_XfeYyZ3v4uiMLA9244tMbWk";

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

    client.subscribe('/topic/admin/b128b828-1e90-48b8-9cc8-2d03013eb092', (message) => {
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
