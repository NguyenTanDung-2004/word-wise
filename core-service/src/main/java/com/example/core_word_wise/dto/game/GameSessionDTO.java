package com.example.core_word_wise.dto.game;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GameSessionDTO {
    private int time;
    private List<GameQuestionDTO> questions;
}