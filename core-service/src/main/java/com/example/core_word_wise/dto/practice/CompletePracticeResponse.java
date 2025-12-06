package com.example.core_word_wise.dto.practice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CompletePracticeResponse {
    private String message;
    @JsonProperty("updated_streak")
    private StreakInfoDTO updatedStreak;
    @JsonProperty("words_update")
    private List<WordUpdateDTO> wordsUpdate;
    private SummaryDTO summary;

    @Data
    @Builder
    public static class WordUpdateDTO {
        private Integer wordId;
        private Float previousScore;
        private Float newScore;
        private LocalDate nextReviewDate;
        private boolean needsReview;
    }

    @Data
    @Builder
    public static class SummaryDTO {
        private int totalWords;
        private int correct;
        private int incorrect;
        private int reviewTomorrow;
    }
}