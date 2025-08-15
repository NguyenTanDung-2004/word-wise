package com.example.WordWise.controller;

import com.example.WordWise.dto.request.AddWordAutomaticallyRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.User;
import com.example.WordWise.entity.Word;
import com.example.WordWise.service.ChatbotService;
import com.example.WordWise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    @Autowired
    private UserService userService;

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/add-word-automatically")
    public ResponseEntity addWordAutomatically(Authentication authentication, @RequestBody AddWordAutomaticallyRequest request) {
        String userId = userService.getUserFromAuthentication(authentication);
        Word word = chatbotService.addWordAutomatically(userId, request.getPrompt());

        ApiResponse apiResponse = ApiResponse.builder()
                .object(word)
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/TEST")
    public ResponseEntity test(Authentication authentication) {
        String userId = userService.getUserFromAuthentication(authentication);
        User user = userService.getUserFromId(userId);
        return ResponseEntity.ok(user);
    }
}
