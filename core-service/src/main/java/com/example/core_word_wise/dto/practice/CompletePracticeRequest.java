package com.example.core_word_wise.dto.practice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CompletePracticeRequest {
    @NotBlank
    private String sessionId;
    @NotEmpty
    private List<WordResult> results;

    @Data
    public static class WordResult {
        private Integer wordId;
        @JsonProperty("learn_count")
        private int learnCount; // Dùng để tính toán độ chính xác
    }
}