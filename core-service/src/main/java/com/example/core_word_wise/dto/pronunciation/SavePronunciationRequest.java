package com.example.core_word_wise.dto.pronunciation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class SavePronunciationRequest {
    @JsonProperty("session_summary")
    private SessionSummary summary;
    private List<Result> results;

    @Data
    public static class SessionSummary {
        private int totalSentences;
        private int completed;
        private double avgScore;
    }
    @Data
    public static class Result {
        private String sentenceId;
        private String userAudioUrl;
        private double score;
        private Feedback feedback;
    }
    @Data
    public static class Feedback {
        private String overall;
        private List<String> problemSounds;
        private List<String> missedWords;
    }
}