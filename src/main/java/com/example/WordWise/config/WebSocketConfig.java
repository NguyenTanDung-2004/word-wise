package com.example.WordWise.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        /*
         * - The above line is used to set the prefix of topic
         * + with this prefix, Our server can have many topics, such as:
         * /topic/notification, /topic/chat, ...
         * - The broker:
         * + Scenario: client 1 send message to 1 topic (/topic/notification)
         * + broker is responsible to send this message to all client who subcribed this
         * topic (/topic/notification)
         */
        config.setApplicationDestinationPrefixes("/app");
        /*
         * - The above line is used to set the destination prefix for sending message by
         * client
         * + with This prefix, client can send to many endpoints, such as: /app/chat,
         * app/notification, ...
         */
    }


    /* user can use this endpoint to connect to websocket */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket endpoint
                .setAllowedOriginPatterns("*") // Allow all origins
                .addInterceptors(customHandshakeInterceptor)
                .withSockJS();
    }
}
