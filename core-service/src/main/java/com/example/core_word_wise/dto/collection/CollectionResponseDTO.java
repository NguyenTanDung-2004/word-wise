package com.example.core_word_wise.dto.collection;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CollectionResponseDTO {
    private Integer collectionId;
    private Integer userId;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
}