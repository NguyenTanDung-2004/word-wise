package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_session_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSessionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "previous_score", nullable = false)
    private Float previousScore;

    @Column(name = "new_score", nullable = false)
    private Float newScore;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}