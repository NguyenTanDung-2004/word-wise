package com.example.core_word_wise.config;

import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.security.JwtService;
import com.example.core_word_wise.security.StompPrincipal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            logger.info("===== Interceptor handling CONNECT command =====");
            try {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String jwt = authHeader.substring(7);
                    String userEmail = jwtService.extractUsername(jwt);
                    if (userEmail != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            User user = (User) userDetails;
                            StompPrincipal principal = new StompPrincipal(user.getUserId().toString());
                            accessor.setUser(principal);
                            logger.info("===== WebSocket session authenticated for user ID: {} =====", principal.getName());
                        } else {
                            logger.warn("WebSocket CONNECT failed: Invalid JWT token.");
                        }
                    }
                } else {
                    logger.warn("WebSocket CONNECT failed: Missing Authorization header.");
                }
            } catch (Exception e) {
                logger.error("!!! EXCEPTION during WebSocket CONNECT: {}", e.getMessage());
            }
        }
        return message;
    }
}