package com.example.core_word_wise.dto.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollectionRequest {
    @NotBlank(message = "Collection name is required")
    private String name;
    private String description;
}