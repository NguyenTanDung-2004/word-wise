package com.example.core_word_wise.dto.chat;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long messageId;
    private Integer senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isSender;
}