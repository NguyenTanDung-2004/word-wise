package com.example.core_word_wise.dto.collection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionSummaryDTO {
    private Integer id;
    private String name;
    private Long wordCount;
    private String lastStudied; // Ví dụ: "3d ago", "today"
}