package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.game.*;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.gamemanager.GameManager;
import com.example.core_word_wise.gamemanager.GameState;
import com.example.core_word_wise.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final WordRepository wordRepository;
    private final GameResultRepository gameResultRepository;
    private final GameModeRepository gameModeRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private final ChallengeRoomRepository roomRepository;
    private final ChallengeRoomParticipantRepository participantRepository;
    private final GameManager gameManager;
    private final TaskScheduler taskScheduler;
    private final UserRepository userRepository;

    private static final int GAME_TIME_SECONDS = 90; // 1 phút 30 giây
    private static final int WORD_COUNT = 10;


    // hàm bắt đầu game real time
    @Transactional
    public GameSessionDTO startGame(Integer roomId, Integer hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new EntityNotFoundException("Host user not found."));

        ChallengeRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found."));

        if (room.getStatus() != ChallengeRoom.RoomStatus.WAITING) {
            throw new IllegalStateException("Game has already started or finished.");
        }

        room.setStatus(ChallengeRoom.RoomStatus.IN_PROGRESS);
        room.setStartedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Lấy chế độ game và gọi hàm lấy câu hỏi tương ứng
        GameSessionDTO gameSession;
        String gameModeName = room.getGameMode().getName();

        switch (gameModeName) {
            case "fill-the-blank":
                gameSession = getFillTheBlankGame();
                break;
            case "word-shooter":
                gameSession = getWordShooterGame();
                break;
            case "definition-match":
            default:
                gameSession = getDefinitionGame();
                break;
        }

        GameState gameState = new GameState();
        gameState.setRoom(room);
        gameState.setQuestions(gameSession.getQuestions());

        List<ChallengeRoomParticipant> participants = participantRepository.findAllByRoom(room);
        participants.forEach(p -> {
            PlayerScore score = new PlayerScore();
            score.setUserId(p.getUser().getUserId());
            score.setUsername(p.getUser().getDisplayName());
            gameState.getPlayerScores().put(p.getUser().getUserId(), score);
        });

        gameManager.addGame(roomId, gameState);

        String destination = "/topic/challenge-room/" + roomId;
        messagingTemplate.convertAndSend(destination, Map.of("type", "GAME_STARTED", "gameSession", gameSession));

        Runnable endGameTask = () -> endGame(roomId);
        ScheduledFuture<?> task = taskScheduler.schedule(endGameTask, Instant.now().plusSeconds(gameSession.getTime() + 3));
        gameState.setGameOverTask(task);

        return gameSession;
    }
    // cập nhật điểm real time
    public void updateUserScore(Integer roomId, Integer userId, int newScore) {
        GameState gameState = gameManager.getGame(roomId);
        if (gameState == null || gameState.getRoom().getStatus() != ChallengeRoom.RoomStatus.IN_PROGRESS) return;

        PlayerScore playerScore = gameState.getPlayerScores().get(userId); // Dùng userId để tìm
        if (playerScore != null) {
            playerScore.setScore(newScore);
            broadcastScoreboard(roomId, gameState);
        }
    }

    // hàm kết thúc game
    @Transactional
    public void endGame(Integer roomId) {
        GameState gameState = gameManager.getGame(roomId);
        if (gameState == null) return;

        ChallengeRoom room = gameState.getRoom();
        if (room.getStatus() == ChallengeRoom.RoomStatus.FINISHED) return;

        room.setStatus(ChallengeRoom.RoomStatus.FINISHED);
        room.setFinishedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Tính thời gian chơi thực tế (nếu StartedAt và FinishedAt không null)
        Integer timeTaken;
        if (room.getStartedAt() != null && room.getFinishedAt() != null) {
            Duration duration = Duration.between(room.getStartedAt(), room.getFinishedAt());
            timeTaken = (int) duration.getSeconds();
        } else {
            timeTaken = null;
        }

        // Tổng số câu hỏi
        int totalQuestions = gameState.getQuestions().size();
        int maxScore = totalQuestions * 10;

        gameState.getPlayerScores().forEach((userId, playerScore) -> {
            GameResult result = new GameResult();
            userRepository.findById(userId).ifPresent(result::setUser);
            result.setGameMode(room.getGameMode());
            result.setScore(playerScore.getScore());

            // TÍNH ACCURACY (Độ chính xác)
            float accuracy = 0f;
            if (maxScore > 0) {
                // (Score / MaxScore) * 100
                accuracy = ((float) playerScore.getScore() / (float) maxScore) * 100f;
            }

            result.setTimeTakenSeconds(timeTaken);
            result.setAccuracy(accuracy);

            gameResultRepository.save(result);
        });

        String destination = "/topic/challenge-room/" + room.getRoomId();
        messagingTemplate.convertAndSend(destination, Map.of("type", "GAME_OVER", "scoreboard", getSortedScores(gameState)));

        gameManager.removeGame(roomId);
    }
    // bảng điểm
    private void broadcastScoreboard(Integer roomId, GameState gameState) {
        String destination = "/topic/challenge-room/" + roomId;
        messagingTemplate.convertAndSend(destination, Map.of("type", "SCOREBOARD_UPDATE", "scoreboard", getSortedScores(gameState)));
    }

    // sắp xếp
    private List<PlayerScore> getSortedScores(GameState gameState) {
        return gameState.getPlayerScores().values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .collect(Collectors.toList());
    }

    // Lấy câu hỏi cho Definition Game
    public GameSessionDTO getDefinitionGame() {
        List<Word> words = wordRepository.findRandomWords(WORD_COUNT * 4); // Lấy nhiều hơn để tạo options
        List<GameQuestionDTO> questions = words.stream().limit(WORD_COUNT).map(correctWord -> {
            List<String> options = words.stream()
                    .filter(w -> !w.equals(correctWord))
                    .map(Word::getDefinitionEn)
                    .limit(3)
                    .collect(Collectors.toList());
            options.add(correctWord.getDefinitionEn());
            Collections.shuffle(options);
            return GameQuestionDTO.builder()
                    .word(correctWord.getWordText())
                    .definition(correctWord.getDefinitionEn()) // Đáp án đúng
                    .options(options)
                    .build();
        }).collect(Collectors.toList());
        return GameSessionDTO.builder().time(GAME_TIME_SECONDS).questions(questions).build();
    }

    // Lấy câu hỏi cho Fill the Blank (logic này cần câu ví dụ có sẵn)
    public GameSessionDTO getFillTheBlankGame() {
        // Lấy nhiều từ hơn để làm đáp án sai
        List<Word> allWords = wordRepository.findRandomWords(WORD_COUNT * 4);

        // Chọn ra 20 từ chính để tạo câu hỏi
        List<Word> questionWords = allWords.stream().limit(WORD_COUNT).toList();

        List<GameQuestionDTO> questions = questionWords.stream().map(correctWord -> {
            try {
                JsonNode examplesNode = objectMapper.readTree(correctWord.getExamples());
                if (examplesNode.isArray() && !examplesNode.isEmpty()) {
                    String exampleEn = examplesNode.get(0).get("en").asText();
                    String sentence = exampleEn.replaceAll("(?i)\\b" + correctWord.getWordText() + "\\b", "___");

                    // TẠO CÁC LỰA CHỌN (OPTIONS)
                    // 1. Lấy 3 từ ngẫu nhiên khác làm đáp án sai
                    List<String> options = allWords.stream()
                            .filter(w -> !w.getWordId().equals(correctWord.getWordId()))
                            .map(Word::getWordText)
                            .limit(3)
                            .collect(Collectors.toList());

                    // 2. Thêm đáp án đúng vào
                    options.add(correctWord.getWordText());

                    // 3. Xáo trộn các lựa chọn
                    Collections.shuffle(options);

                    return GameQuestionDTO.builder()
                            .sentence(sentence)
                            .options(options) // Thêm options
                            .answer(correctWord.getWordText())
                            .build();
                }
            } catch (Exception e) {
                // Bỏ qua
            }
            return null;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        return GameSessionDTO.builder().time(GAME_TIME_SECONDS).questions(questions).build();
    }

    // Lấy câu hỏi cho Word Shooter
    public GameSessionDTO getWordShooterGame() {
        List<Word> words = wordRepository.findRandomWords(WORD_COUNT);
        List<GameQuestionDTO> questions = words.stream().map(word ->
                GameQuestionDTO.builder()
                        .en(word.getWordText())
                        .vi(word.getWordVn())
                        .build()
        ).collect(Collectors.toList());
        return GameSessionDTO.builder().time(GAME_TIME_SECONDS).questions(questions).build();
    }

    // Submit kết quả game
    @Transactional
    public LeaderboardResponseDTO submitGameResult(User user, SubmitGameRequest request) {
        GameMode gameMode = gameModeRepository.findByName(request.getGameMode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid game mode."));

        GameResult result = new GameResult();
        result.setUser(user);
        result.setGameMode(gameMode);
        result.setScore(request.getScore());
        result.setAccuracy((float) request.getAccuracy());
        result.setTimeTakenSeconds(request.getTimeTakenSeconds());
        gameResultRepository.save(result);

        return getLeaderboard(user, request.getGameMode());
    }

    // Lấy bảng xếp hạng
    public LeaderboardResponseDTO getLeaderboard(User user, String gameModeName) {
        GameMode gameMode = gameModeRepository.findByName(gameModeName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game mode."));

        Integer gameModeId = gameMode.getGameModeId();

        // 1. Lấy MAX SCORE cho mỗi người dùng (Top 100 theo điểm số)
        List<BestScoreProjection> bestScores = gameResultRepository.findMaxScoresByGameMode(
                gameModeId,
                PageRequest.of(0, 100)
        );

        List<Integer> userIds = bestScores.stream().map(BestScoreProjection::getUserId).toList();
        List<Integer> scores = bestScores.stream().map(BestScoreProjection::getMaxScore).toList();

        if (userIds.isEmpty()) {
            return LeaderboardResponseDTO.builder()
                    .gameMode(gameModeName)
                    .top3Players(List.of())
                    .fullLeaderboard(List.of())
                    .chartData(LeaderboardResponseDTO.ChartData.builder().labels(List.of()).scores(List.of()).build())
                    .build();
        }

        // 2. Lấy bản ghi GameResult đầy đủ (chỉ lấy các bản ghi có điểm tối đa đó)
        List<GameResult> topResults = gameResultRepository.findBestResultsByScoreAndUser(
                gameModeId,
                userIds,
                scores
        );

        // 3. Xếp hạng và ánh xạ DTO
        List<LeaderboardEntryDTO> fullLeaderboard = new ArrayList<>();
        Map<Integer, LeaderboardEntryDTO> uniqueRankedEntries = new LinkedHashMap<>();
        LeaderboardEntryDTO currentUserEntry = null;

        for (GameResult result : topResults) {
            if (!uniqueRankedEntries.containsKey(result.getUser().getUserId())) {
                int currentRank = uniqueRankedEntries.size() + 1;
                LeaderboardEntryDTO entry = mapToLeaderboardEntryDTO(result, currentRank);
                uniqueRankedEntries.put(result.getUser().getUserId(), entry);
                fullLeaderboard.add(entry);

                if (result.getUser().getUserId().equals(user.getUserId())) {
                    currentUserEntry = entry;
                }
            }
        }

        // 4. Tách top 3 và tạo biểu đồ
        List<LeaderboardEntryDTO> top3Players = fullLeaderboard.stream().limit(3).collect(Collectors.toList());

        LeaderboardResponseDTO.ChartData chartData = LeaderboardResponseDTO.ChartData.builder()
                .labels(top3Players.stream().map(LeaderboardEntryDTO::getUsername).collect(Collectors.toList()))
                .scores(top3Players.stream().map(LeaderboardEntryDTO::getScore).collect(Collectors.toList()))
                .build();

        return LeaderboardResponseDTO.builder()
                .gameMode(gameModeName)
                .currentUser(currentUserEntry)
                .top3Players(top3Players)
                .fullLeaderboard(fullLeaderboard.stream().skip(3).collect(Collectors.toList()))
                .chartData(chartData)
                .build();
    }

    public void inviteFriendsToGame(User inviter, InviteGameRequest request) {
        String gameMode = request.getGameMode();
        List<Integer> friendIds = request.getFriendIds();

        friendIds.forEach(friendId -> {
            String destination = "/queue/notifications";

            Map<String, Object> payload = Map.of(
                    "type", "GAME_INVITE",
                    "inviterId", inviter.getUserId(),
                    "inviterName", inviter.getDisplayName(),
                    "gameMode", gameMode,
                    "message", inviter.getDisplayName() + " has invited you to a game of " + gameMode + "!"
            );

            // Gửi thông báo qua WebSocket
            messagingTemplate.convertAndSendToUser(friendId.toString(), destination, payload);
        });
    }

    // Hàm helper để chuyển đổi
    private LeaderboardEntryDTO mapToLeaderboardEntryDTO(GameResult result, int rank) {
        Integer secondsTotal = result.getTimeTakenSeconds();
        String timeFormatted;

        if (secondsTotal == null) {
            timeFormatted = "N/A";
            secondsTotal = 0;
        } else {
            int minutes = secondsTotal / 60;
            int seconds = secondsTotal % 60;
            timeFormatted = String.format("%02d:%02d", minutes, seconds);
        }

        Double accuracy = result.getAccuracy() != null ? Double.valueOf(result.getAccuracy()) : 0.0;

        String badgeColor = null;
        if (rank == 1) badgeColor = "#EBAD25";
        else if (rank == 2) badgeColor = "#C0C0C0";
        else if (rank == 3) badgeColor = "#CD7F32";

        return LeaderboardEntryDTO.builder()
                .rank(rank)
                .username(result.getUser().getDisplayName())
                .avatarUrl(result.getUser().getAvatarUrl())
                .score(result.getScore())
                .accuracy(accuracy)
                .time(timeFormatted)
                .badgeColor(badgeColor)
                .build();
    }
}