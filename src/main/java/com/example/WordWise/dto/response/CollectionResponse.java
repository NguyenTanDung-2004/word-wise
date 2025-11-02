package com.example.WordWise.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CollectionResponse {
    private String id;
    private String userId;
    private String collectionName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Integer wordCount;
}

