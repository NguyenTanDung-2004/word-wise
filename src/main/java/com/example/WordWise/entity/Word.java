package com.example.WordWise.entity;

import java.sql.Date;
import java.time.LocalDateTime;

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
}
