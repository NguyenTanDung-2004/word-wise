package com.example.core_word_wise.dto.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GeneralStatsResponse {
    @JsonProperty("user_id")
    private Integer userId;
    private Overview overview;
    @JsonProperty("learning_trend")
    private List<TrendPoint> learningTrend;
    @JsonProperty("most_forgotten_words")
    private List<ForgottenWord> mostForgottenWords;
    private PronunciationStats pronunciation;

    @Data
    @Builder
    public static class Overview {
        private long totalWordsLearned;
        private long wordsReviewedToday;
        private double retentionRate;
    }

    @Data
    @Builder
    public static class TrendPoint {
        private String day; // "Sun", "Mon", hoặc "2025-11-01"
        private double score;
    }

    @Data
    @Builder
    public static class ForgottenWord {
        private String word;
        private String partOfSpeech;
    }

    @Data
    @Builder
    public static class PronunciationStats {
        private double accuracy;
        @JsonProperty("improvement_trend")
        private List<TrendPoint> improvementTrend;
    }
}