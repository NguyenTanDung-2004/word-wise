package com.example.core_word_wise.dto.collection;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordResponseDTO {
    private Integer wordId;
    private String wordText;
    private String wordVn;
    private String partOfSpeech;
    private String definitionEn;
    private String definitionVi;
    private String sourceUrl;
    private JsonNode phonetics;
    private JsonNode examples;
    private JsonNode idiomsCollocations;
    private JsonNode phrasalVerbs;
    private String synonyms;
}