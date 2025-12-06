package com.example.core_word_wise.dto.stats;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GeneralStatsRequest {
    private String rangeType; // week, month, year
    private LocalDate startDate;
    private LocalDate endDate;
}