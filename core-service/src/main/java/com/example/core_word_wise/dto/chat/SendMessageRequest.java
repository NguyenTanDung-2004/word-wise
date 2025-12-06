package com.example.core_word_wise.dto.chat;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Integer receiverId;
    private String content;
}