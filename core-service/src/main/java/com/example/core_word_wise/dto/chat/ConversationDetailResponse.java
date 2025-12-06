package com.example.core_word_wise.dto.chat;

import com.example.core_word_wise.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConversationDetailResponse {
    private Integer conversationId;
    private List<UserResponse> participants;
    private List<MessageResponse> messages;
}