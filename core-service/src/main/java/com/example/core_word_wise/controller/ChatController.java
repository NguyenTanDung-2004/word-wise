package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.chat.ConversationDetailResponse;
import com.example.core_word_wise.dto.chat.MessageResponse;
import com.example.core_word_wise.dto.chat.SendMessageRequest;
import com.example.core_word_wise.dto.chat.TopicChatResponse;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Lấy danh sách topic chat
    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<TopicChatResponse>>> getChatTopics(@AuthenticationPrincipal User user) {
        List<TopicChatResponse> topics = chatService.getChatTopics(user);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    // Lấy tin nhắn của một topic
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> getConversationMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Integer conversationId
    ) {
        ConversationDetailResponse details = chatService.getConversationDetails(user, conversationId);
        return ResponseEntity.ok(ApiResponse.success(details));
    }


    // Gửi tin nhắn
    @PostMapping("/send/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @AuthenticationPrincipal User sender,
            @Valid @RequestBody SendMessageRequest request
    ) {
        MessageResponse sentMessage = chatService.sendMessage(sender, request.getReceiverId(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(sentMessage, "Message sent successfully."));
    }
}