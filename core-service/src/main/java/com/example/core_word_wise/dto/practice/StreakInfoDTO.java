package com.example.core_word_wise.dto.practice;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class StreakInfoDTO {
    private Integer currentStreakDays;
    private Integer longestStreakDays;
    private LocalDate lastStudyDate;
}