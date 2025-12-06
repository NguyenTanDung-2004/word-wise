package com.example.core_word_wise.dto.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameQuestionDTO {
    // Dùng cho Definition Game
    private String word;
    private String definition;
    private List<String> options; // 3 định nghĩa sai + 1 đúng

    // Dùng cho Fill the Blank
    private String sentence;
    private String answer;

    // Dùng cho Word Shooter
    private String en;
    private String vi;
}