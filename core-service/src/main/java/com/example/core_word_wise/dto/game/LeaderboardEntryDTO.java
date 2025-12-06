package com.example.core_word_wise.dto.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDTO {
    private int rank;
    private String username;
    private String avatarUrl;
    private int score;
    private Double accuracy;
    private String time;
    private String badgeColor;
}