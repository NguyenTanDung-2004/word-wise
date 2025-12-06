package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "learning_streak")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStreak {
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

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Version
    @Column(name = "version")
    private Long version;

    public boolean isStreakActive() {
        if (lastStudyDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();

        long daysDifference = ChronoUnit.DAYS.between(lastStudyDate, today);

        // Streak còn active nếu ngày học cuối cùng là hôm nay (daysDifference == 0)
        // HOẶC ngày học cuối cùng là hôm qua (daysDifference == 1).
        // Nếu daysDifference > 1, streak đã bị đứt.
        return daysDifference <= 1;
    }
}