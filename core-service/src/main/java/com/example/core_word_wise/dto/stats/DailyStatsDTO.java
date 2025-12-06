package com.example.core_word_wise.dto.stats;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyStatsDTO {
    private LocalDate date;
    private int totalWordsStudied;
    private int totalCorrect;
    private int totalIncorrect;
    private double accuracy;
    private int sessionCount;
    private List<SessionSummaryDTO> sessions;
    private Integer reviewTomorrow;
}