package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_mode")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_mode_id")
    private Integer gameModeId;

    @Column(length = 50, nullable = false, unique = true)
    private String name;
}