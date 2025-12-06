package com.example.core_word_wise.dto.collection;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CollectionDetailDTO {
    private Integer collectionId;
    private String name;
    private String description;
    private Long totalWords;
    private LocalDateTime createdAt;
    private LocalDateTime lastStudiedAt;
    private List<WordDetailDTO> words;
}