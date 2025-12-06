package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_setting")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "study_sessions_per_day")
    private Integer studySessionsPerDay;

    @Column(name = "words_per_session")
    private Integer wordsPerSession;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Version
    private Integer version;
}