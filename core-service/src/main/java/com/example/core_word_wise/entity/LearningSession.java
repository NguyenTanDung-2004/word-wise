package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSession {

    public enum SessionStatus {
        PENDING,
        COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionId;

    @Column(name = "session_uuid", unique = true, nullable = false)
    private String sessionUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;

    @Column(name = "is_daily_streak_session", nullable = false)
    private Boolean isDailyStreakSession = false;

    @Column(name = "session_date")
    private LocalDateTime sessionDate = LocalDateTime.now();

    @Column(name = "total_words")
    private Integer totalWords;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "incorrect_count")
    private Integer incorrectCount;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LearningSessionDetail> details = new ArrayList<>();
}