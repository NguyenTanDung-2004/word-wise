package com.example.core_word_wise.dto.game;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LeaderboardResponseDTO {
    private String gameMode;
    private LeaderboardEntryDTO currentUser;
    private List<LeaderboardEntryDTO> top3Players;
    private List<LeaderboardEntryDTO> fullLeaderboard; // Từ rank 4 trở đi
    private ChartData chartData;

    @Data
    @Builder
    public static class ChartData {
        private List<String> labels;
        private List<Integer> scores;
    }
}