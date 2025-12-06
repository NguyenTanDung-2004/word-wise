package com.example.core_word_wise.dto.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CollectionProgressDTO {
    @JsonProperty("progress_chart")
    private ChartData progressChart;

    @JsonProperty("last_reviewed_on")
    private LocalDate lastReviewedOn;

    private long totalWords;
    private double averageScore;

    @Data
    @Builder
    public static class ChartData {
        // Ví dụ: ["New", "Learning", "Mastered"]
        private List<String> labels;
        // Dữ liệu cho từng cấp độ
        private List<LevelData> data;
    }

    @Data
    @Builder
    public static class LevelData {
        private String levelName;
        private long wordCount;
    }
}