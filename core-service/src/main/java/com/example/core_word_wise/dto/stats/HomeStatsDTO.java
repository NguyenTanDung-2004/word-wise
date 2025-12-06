package com.example.core_word_wise.dto.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeStatsDTO {
    @JsonProperty("total_words")
    private Long totalWords;

    @JsonProperty("total_collections")
    private Long totalCollections;

    @JsonProperty("today_words")
    private Long todayWords;

    @JsonProperty("learning_streak")
    private Integer learningStreak;
}