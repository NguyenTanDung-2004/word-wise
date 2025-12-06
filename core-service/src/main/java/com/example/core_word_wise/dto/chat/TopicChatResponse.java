package com.example.core_word_wise.dto.chat;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TopicChatResponse {
    private Integer conversationId;
    private Integer otherUserId;
    private String name;
    private String avatar;
    private String lastMessage;
    private LocalDateTime time;
    private long unreadCount;
    private boolean isOnline;
}