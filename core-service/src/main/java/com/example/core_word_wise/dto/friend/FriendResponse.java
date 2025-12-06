package com.example.core_word_wise.dto.friend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendResponse {
    private Integer userId;
    private String username;
    private String avatarUrl;
}