package com.example.core_word_wise.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserResponse {
    private UserResponse userInfo;
    private String newToken;
}