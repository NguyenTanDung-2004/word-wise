package com.example.WordWise.config;


import com.example.WordWise.model.redis_object.CreatePracticeRoomRedisObject;
import com.example.WordWise.utils.JwtUtils;
import com.example.WordWise.utils.RedisUtils;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        URI uri = request.getURI();
        String query = uri.getQuery(); // e.g. token=eyJhbGc...
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    break;
                }
            }
        }

        if (token == null) {
            return false; // reject if no token
        }

        Map<String, Object> decodedMap = this.jwtUtils.decodeHandShakeToken(token);
        String userId = (String) decodedMap.get("userId");
        String channel = (String) decodedMap.get("channel");
        String action = (String) decodedMap.get("action");

        if (userId == null || channel == null || action == null) {
            return false;
        }

        return redisUtils.filterToHandShake(userId, channel, action);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
       // TODO
    }
}