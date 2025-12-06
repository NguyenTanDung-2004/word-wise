package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_room_participant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRoomParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer participantId;

    @ManyToOne @JoinColumn(name = "room_id", nullable = false)
    private ChallengeRoom room;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer score = 0;
    private LocalDateTime joinedAt = LocalDateTime.now();
}