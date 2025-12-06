package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pronunciation_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "session_date")
    private LocalDateTime sessionDate;

    @Column(name = "user_audio_url", length = 500)
    private String userAudioUrl;

    private Float score;

    @Column(name = "feedback_overall", columnDefinition = "TEXT")
    private String feedbackOverall;

    @Column(name = "problem_sounds", columnDefinition = "json")
    private String problemSounds;

    @Column(name = "missed_words", columnDefinition = "json")
    private String missedWords;

    private String sentenceId;
}