package com.example.core_word_wise.gamemanager;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {
    private final Map<Integer, GameState> activeGames = new ConcurrentHashMap<>();

    public void addGame(Integer roomId, GameState gameState) {
        activeGames.put(roomId, gameState);
    }

    public GameState getGame(Integer roomId) {
        return activeGames.get(roomId);
    }

    public void removeGame(Integer roomId) {
        GameState gameState = activeGames.remove(roomId);
        if (gameState != null && gameState.getGameOverTask() != null) {
            gameState.getGameOverTask().cancel(false);
        }
    }
}