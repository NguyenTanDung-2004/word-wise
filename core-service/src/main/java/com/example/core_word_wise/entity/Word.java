package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "word")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wordId;

    @Column(name = "word_text", unique = true, nullable = false)
    private String wordText;

    @Column(name = "word_vn")
    private String wordVn;

    @Column(name = "part_of_speech")
    private String partOfSpeech;

    @Column(name = "definition_en", columnDefinition = "TEXT")
    private String definitionEn;

    @Column(name = "definition_vi", columnDefinition = "TEXT")
    private String definitionVi;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(columnDefinition = "JSON")
    private String phonetics;

    @Column(columnDefinition = "JSON")
    private String examples;

    @Column(name = "idioms_collocations", columnDefinition = "JSON")
    private String idiomsCollocations;

    @Column(name = "phrasal_verbs", columnDefinition = "JSON")
    private String phrasalVerbs;

    private String synonyms;
}