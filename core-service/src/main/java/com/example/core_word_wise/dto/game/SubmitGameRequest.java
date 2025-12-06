package com.example.core_word_wise.dto.game;

import lombok.Data;

@Data
public class SubmitGameRequest {
    private String gameMode; // "definition-match", "fill-the-blank", "word-shooter"
    private int score;
    private double accuracy; // 0.0 to 100.0
    private int timeTakenSeconds;
}