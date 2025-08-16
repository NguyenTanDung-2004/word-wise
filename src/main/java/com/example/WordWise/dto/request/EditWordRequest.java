package com.example.WordWise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditWordRequest {
    private String wordId;
    private String context;
    private Boolean isExtension;
    private String englishWord;
    private String vietnameseWord;
    private String note;

    // additional fields for adding word originally
    private String idiom;
    private String example;
    private String phonetic;
    private String partOfSpeech;
    private String audioUrl;
}
