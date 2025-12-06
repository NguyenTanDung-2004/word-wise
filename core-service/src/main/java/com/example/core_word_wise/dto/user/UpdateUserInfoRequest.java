package com.example.core_word_wise.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserInfoRequest {
    private String avatar;
    private String username;
}