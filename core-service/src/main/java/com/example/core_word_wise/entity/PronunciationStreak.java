package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pronunciation_streak")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationStreak {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_streak_days")
    private Integer currentStreakDays;

    @Column(name = "longest_streak_days")
    private Integer longestStreakDays;

    @Column(name = "last_practice_date")
    private LocalDate lastPracticeDate;

    @Version
    private Integer version;
}