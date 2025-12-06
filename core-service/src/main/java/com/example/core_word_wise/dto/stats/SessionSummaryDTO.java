package com.example.core_word_wise.dto.stats;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SessionSummaryDTO {
    private Integer sessionId;
    private String sessionUuid;
    private LocalDateTime sessionDate;
    private Integer totalWords;
    private Integer correctCount;
    private Integer incorrectCount;
    private boolean isDailyStreakSession;
    private List<WordHistoryDTO> wordsStudied;
}