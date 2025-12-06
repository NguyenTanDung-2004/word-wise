package com.example.core_word_wise.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Content cannot be blank")
    private String content;

    @JsonProperty("collection_name")
    private String collectionName; // Optional: Tên collection muốn share
}