package com.example.core_word_wise.dto.practice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PracticeSessionDTO {
    private String sessionId;
    private Integer userId;
    private LocalDate date;
    @JsonProperty("streak_info")
    private StreakInfoDTO streakInfo;
    @JsonProperty("list_words")
    private List<PracticeWordDTO> listWords;
    private CollectionInfoDTO collection;

    @Data
    @Builder
    public static class CollectionInfoDTO {
        private Integer collectionId;
        private String collectionName;
        private Integer totalWords;
    }
}