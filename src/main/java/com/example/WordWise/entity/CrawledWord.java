package com.example.WordWise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_words")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrawledWord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String english;
    private String vietnamese;
    private String engUsage;
    private String vnUsage;
    private String engSentence;
    private String vnSentence;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String link;
    private String trendingNewFeedId;
}
