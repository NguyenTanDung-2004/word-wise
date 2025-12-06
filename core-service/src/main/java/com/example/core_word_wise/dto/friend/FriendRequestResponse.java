package com.example.core_word_wise.dto.friend;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FriendRequestResponse {
    private Integer friendshipId;
    private Integer requesterId;
    private String requesterUsername;
    private String requesterAvatarUrl;
    private LocalDateTime requestDate;
}