package com.example.core_word_wise.dto.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserSettingDTO {
    @JsonProperty("user_id")
    private Integer userId;

    private Settings settings;

    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;

    @Data
    @Builder
    public static class Settings {
        @JsonProperty("study_sessions_per_day")
        private Integer studySessionsPerDay;

        @JsonProperty("words_per_session")
        private Integer wordsPerSession;
    }
}