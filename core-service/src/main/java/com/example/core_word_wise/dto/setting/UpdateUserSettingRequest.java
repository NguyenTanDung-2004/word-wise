package com.example.core_word_wise.dto.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserSettingRequest {
    @NotNull
    @Min(1)
    @JsonProperty("study_sessions_per_day")
    private Integer studySessionsPerDay;

    @NotNull
    @Min(1)
    @JsonProperty("words_per_session")
    private Integer wordsPerSession;
}