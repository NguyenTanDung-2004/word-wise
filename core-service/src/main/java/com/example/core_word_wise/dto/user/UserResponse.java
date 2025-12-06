package com.example.core_word_wise.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
}