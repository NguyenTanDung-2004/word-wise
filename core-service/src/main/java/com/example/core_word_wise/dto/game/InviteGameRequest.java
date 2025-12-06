package com.example.core_word_wise.dto.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class InviteGameRequest {

    @NotBlank(message = "Game mode is required")
    @JsonProperty("game_mode")
    private String gameMode; // "definition-match", "fill-the-blank", etc.

    @NotEmpty(message = "Friend IDs list cannot be empty")
    @JsonProperty("friend_ids")
    private List<Integer> friendIds;
}