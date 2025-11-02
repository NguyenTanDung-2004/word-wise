package com.example.WordWise.dto.request;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertWordRequest {
    private String userId;
    private String collectionId;
    private String context;
    private Boolean isExtension;
    private String englishWord;
    private String vietnameseWord;
    private String note;
    private String idiom;
    private String example;
    private String phonetic;
    private String partOfSpeech;
}
