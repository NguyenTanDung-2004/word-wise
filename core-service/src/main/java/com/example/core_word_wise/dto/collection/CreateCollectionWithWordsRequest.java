package com.example.core_word_wise.dto.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateCollectionWithWordsRequest {

    @NotBlank(message = "Collection name is required")
    @JsonProperty("collection")
    private String collectionName;

    private String description;

    @NotEmpty(message = "Word list cannot be empty")
    @JsonProperty("list_words")
    private List<@Valid WordDTO> words;
}