package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.game.GameSessionDTO;
import com.example.core_word_wise.dto.game.InviteGameRequest;
import com.example.core_word_wise.dto.game.LeaderboardResponseDTO;
import com.example.core_word_wise.dto.game.SubmitGameRequest;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // Lấy đề cho Definition Game
    @GetMapping("/definition-match")
    public ResponseEntity<ApiResponse<GameSessionDTO>> getDefinitionGame() {
        return ResponseEntity.ok(ApiResponse.success(gameService.getDefinitionGame()));
    }

    // Lấy đề cho Word Shooter
    @GetMapping("/word-shooter")
    public ResponseEntity<ApiResponse<GameSessionDTO>> getWordShooterGame() {
        return ResponseEntity.ok(ApiResponse.success(gameService.getWordShooterGame()));
    }
    // Lấy để cho fill the blank
    @GetMapping("/fill-the-blank")
    public ResponseEntity<ApiResponse<GameSessionDTO>> getFillTheBlankGame() {
        return ResponseEntity.ok(ApiResponse.success(gameService.getFillTheBlankGame()));
    }

    // Submit kết quả và nhận về leaderboard
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<LeaderboardResponseDTO>> submitGame(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmitGameRequest request
    ) {
        LeaderboardResponseDTO leaderboard = gameService.submitGameResult(user, request);
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Game result submitted."));
    }

    // Lấy leaderboard theo game mode
    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<LeaderboardResponseDTO>> getLeaderboard(
            @AuthenticationPrincipal User user,
            @RequestParam String gameMode
    ) {
        LeaderboardResponseDTO leaderboard = gameService.getLeaderboard(user, gameMode);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    // API mời bạn chơi game
    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Void>> inviteFriends(
            @AuthenticationPrincipal User inviter,
            @Valid @RequestBody InviteGameRequest request
    ) {
        gameService.inviteFriendsToGame(inviter, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Game invitations sent successfully."));
    }

    // start real time game
    @GetMapping("/challenge-room/{roomId}/start")
    public ResponseEntity<ApiResponse<GameSessionDTO>> startGame(
            @PathVariable Integer roomId,
            @AuthenticationPrincipal User host
    ) {
        GameSessionDTO gameSession = gameService.startGame(roomId, host.getUserId());
        return ResponseEntity.ok(ApiResponse.success(gameSession, "Game started."));
    }
}