package com.example.core_word_wise.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordHistoryDTO {
    private Integer wordId;
    private String wordText;
    private Float previousScore;
    private Float newScore;
    private boolean isCorrect;
}