package com.example.core_word_wise.dto.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddWordRequest {
    private Integer userId;

    @NotBlank(message = "Collection name is required")
    @JsonProperty("collection")
    private String collectionName;

    @NotNull(message = "Word data is required")
    @Valid
    private List<@Valid WordDTO> words;
}