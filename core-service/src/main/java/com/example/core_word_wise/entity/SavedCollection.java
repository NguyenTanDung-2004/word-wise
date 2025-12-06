package com.example.core_word_wise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_collection")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer savedCollectionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "original_collection_id")
    private Collection originalCollection;

    private LocalDateTime createdAt;

    @Column(name = "last_studied_at")
    private LocalDateTime lastStudiedAt;
}