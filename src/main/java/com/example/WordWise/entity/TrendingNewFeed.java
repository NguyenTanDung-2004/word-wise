package com.example.WordWise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trending_new_feed")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrendingNewFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private String link;
    private String image;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime newfeedDate;
}
