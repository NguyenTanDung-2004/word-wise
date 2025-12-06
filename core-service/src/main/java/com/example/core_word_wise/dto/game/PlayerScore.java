package com.example.core_word_wise.dto.game;

import lombok.Data;

@Data
public class PlayerScore {
    private Integer userId;
    private String username;
    private int score = 0;
    private int streak = 0; // Trả lời đúng liên tiếp
}