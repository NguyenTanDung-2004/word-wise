package com.example.core_word_wise.dto.collection;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateWordDTO {
    private String word;
    private String word_vn;
    private Map<String, WordDTO.PhoneticDTO> phonetics;
    private String partOfSpeech;
    private String definition_en;
    private String definition_vi;
    private List<Map<String, String>> examples;
    private List<Map<String, String>> idioms_collocations;
    private List<String> synonyms;
    private String source;

    @Data
    public static class PhoneticDTO {
        private String text;
        private String audio;
    }
}
