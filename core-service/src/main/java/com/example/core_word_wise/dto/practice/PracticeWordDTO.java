package com.example.core_word_wise.dto.practice;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PracticeWordDTO {
    private Integer wordId;
    private String word;
    private String wordVn;
    private JsonNode phonetics;
    private String partOfSpeech;
    private String definitionEn;
    private String definitionVi;
    private JsonNode examples;
    private JsonNode idiomsCollocations;
    private String synonyms;
    private String source;
    // SRS Fields
    private Float familiarityScore;
    private Integer reviewCount;
    private String priorityLevel;
}