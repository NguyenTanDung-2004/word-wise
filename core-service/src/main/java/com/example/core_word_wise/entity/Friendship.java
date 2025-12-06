package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    public enum FriendshipStatus { PENDING, ACCEPTED, REJECTED, BLOCKED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer friendshipId;

    @ManyToOne @JoinColumn(name = "user_one_id") private User userOne;
    @ManyToOne @JoinColumn(name = "user_two_id") private User userTwo;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @ManyToOne @JoinColumn(name = "action_user_id") private User actionUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}