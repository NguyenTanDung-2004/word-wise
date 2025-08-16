package com.example.WordWise.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String context;
    private Boolean isExtension;
    private String englishWord;
    private String vietnameseWord;
    private String note;
    private String analyzedText;

    // These fields below are used to support for Extension Review feature (TYPE = SELECT)
    private String description;
    private List<String> options;

    // Additional fields for adding word manually
    private String idiom;
    private String example;
    private String phonetic;
    private String partOfSpeech;
    private String audioUrl;
}
