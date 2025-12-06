package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_word")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userWordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "familiarity_score")
    private Float familiarityScore;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level")
    private PriorityLevel priorityLevel;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "saved_context", columnDefinition = "TEXT")
    private String savedContext;

    public enum PriorityLevel {
        LOW, MEDIUM, HIGH
    }
}