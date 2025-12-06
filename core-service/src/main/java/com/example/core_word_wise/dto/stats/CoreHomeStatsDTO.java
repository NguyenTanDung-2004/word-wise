package com.example.core_word_wise.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreHomeStatsDTO {
    private Long totalWords;            // Tổng số từ hiện có (Total Words)
    private Long totalCollections;      // Tổng số collection
    private Long todayWords;            // Tổng số từ cần học hôm nay (Practice Words)
    private Double avgPronunciationScore; // Điểm trung bình phát âm
}