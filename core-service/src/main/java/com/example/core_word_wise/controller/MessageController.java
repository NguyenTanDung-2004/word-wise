package com.example.core_word_wise.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @MessageMapping("/test")
    public void testAuthentication(Principal principal) {
        if (principal != null) {
            logger.info("===== WebSocket Test Endpoint: Principal name is: {} =====", principal.getName());
        } else {
            logger.error("===== WebSocket Test Endpoint: Principal is NULL. Authentication failed. =====");
        }
    }
}