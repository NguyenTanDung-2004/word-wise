package com.example.WordWise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCollectionRequest {
    private String collectionId;
    private String collectionName;
    private String description;
}

