package com.example.WordWise.entity;

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
@Table(name = "collections")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String collectionName;

    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Integer wordCount;
}

