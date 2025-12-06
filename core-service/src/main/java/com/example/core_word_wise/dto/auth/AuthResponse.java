package com.example.core_word_wise.dto.auth;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Integer userId;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private String token;
}