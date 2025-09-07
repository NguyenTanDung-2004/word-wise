package com.example.WordWise.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "practice_room_questions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PracticeRoomQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;
    private LocalDateTime createdDate;
    private String type;
    private String options;
    private String answer;
    private String question;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore
    @ToString.Exclude
    private PracticeTogetherRoom room;
}
