package com.example.core_word_wise.dto.challenge;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinRoomRequest {
    @NotBlank
    private String inviteCode;
}