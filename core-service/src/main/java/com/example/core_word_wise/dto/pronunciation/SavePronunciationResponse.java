package com.example.core_word_wise.dto.pronunciation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SavePronunciationResponse {
    private String status;
    private String message;
    @JsonProperty("session_summary")
    private SessionSummaryResponse summary;
    private List<SavePronunciationRequest.Result> results;

    @Data
    @Builder
    public static class SessionSummaryResponse {
        private int totalSentences;
        private int completed;
        private double avgScore;
        private int passedSentences;
        private int failedSentences;
        @JsonProperty("streak_info")
        private StreakInfo streakInfo;
        @JsonProperty("next_review_date")
        private LocalDate nextReviewDate;
    }

    @Data
    @Builder
    public static class StreakInfo {
        private int currentStreakDays;
        private int longestStreakDays;
    }
}