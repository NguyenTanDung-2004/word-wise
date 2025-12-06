package com.example.core_word_wise.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtensionTokenResponse {
    private String token;
}