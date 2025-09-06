package com.example.WordWise.entity;

import com.example.WordWise.enums.PracticeTogetherRoomStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "practice_together_room")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PracticeTogetherRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;
    private LocalDateTime createdDate;
    private LocalDateTime endDate;
    private LocalDateTime updatedDate;
    private String name;
    private Integer numberOfMembers;
    private Integer duration;
    private Integer numberOfQuestions;
    private Boolean isPublic;
    private String password;

    @Column(columnDefinition = "text")
    private String questions; // json content
    private String roomCode;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @ToString.Exclude
    private User user;

    public PracticeTogetherRoomStatus getStatus() {
        return PracticeTogetherRoomStatus.fromId(status);
    }

    public void setStatus(PracticeTogetherRoomStatus status) {
        this.status = status.getKey();
    }
}
