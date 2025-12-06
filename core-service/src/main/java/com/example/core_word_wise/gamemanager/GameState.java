package com.example.core_word_wise.gamemanager;

import com.example.core_word_wise.dto.game.GameQuestionDTO;
import com.example.core_word_wise.dto.game.PlayerScore;
import com.example.core_word_wise.entity.ChallengeRoom;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Data
public class GameState {
    private ChallengeRoom room;
    private List<GameQuestionDTO> questions;
    private Map<Integer, PlayerScore> playerScores = new ConcurrentHashMap<>();
    private ScheduledFuture<?> gameOverTask;
}