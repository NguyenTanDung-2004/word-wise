package com.example.core_word_wise.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWordResponseDTO {
    private List<String> success;
    private List<FailedWordDTO> failed;

    @Data
    @AllArgsConstructor
    public static class FailedWordDTO {
        private String word;
        private String reason;
    }
}