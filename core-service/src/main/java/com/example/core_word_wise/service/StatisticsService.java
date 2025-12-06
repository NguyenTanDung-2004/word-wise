package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.stats.*;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final LearningSessionRepository learningSessionRepository;
    private final UserWordRepository userWordRepository;
    private final CollectionRepository collectionRepository;
    private final LearningStreakRepository learningStreakRepository;
    private final PronunciationRecordRepository pronunciationRecordRepository;

    @Transactional(readOnly = true)
    public DailyStatsDTO getDailyStatistics(User user, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<LearningSession> sessions = learningSessionRepository.findByUserAndSessionDateBetween(user, startOfDay, endOfDay);

        List<SessionSummaryDTO> sessionSummaries = sessions.stream().map(session -> {
            // Lấy chi tiết các từ đã học trong session này
            List<WordHistoryDTO> wordsStudied = new ArrayList<>();
            if (session.getDetails() != null) {
                wordsStudied = session.getDetails().stream()
                        .map(detail -> WordHistoryDTO.builder()
                                .wordId(detail.getWord().getWordId())
                                .wordText(detail.getWord().getWordText())
                                .previousScore(detail.getPreviousScore())
                                .newScore(detail.getNewScore())
                                .isCorrect(detail.getIsCorrect())
                                .build())
                        .collect(Collectors.toList());
            }

            return SessionSummaryDTO.builder()
                    .sessionId(session.getSessionId())
                    .sessionUuid(session.getSessionUuid())
                    .sessionDate(session.getSessionDate())
                    .totalWords(session.getTotalWords())
                    .correctCount(session.getCorrectCount())
                    .incorrectCount(session.getIncorrectCount())
                    .isDailyStreakSession(session.getIsDailyStreakSession())
                    .wordsStudied(wordsStudied) // Thêm danh sách từ
                    .build();
        }).collect(Collectors.toList());

        int totalWords = sessions.stream()
                .mapToInt(s -> s.getTotalWords() != null ? s.getTotalWords() : 0)
                .sum();
        int totalCorrect = sessions.stream()
                .mapToInt(s -> s.getCorrectCount() != null ? s.getCorrectCount() : 0)
                .sum();
        double accuracy = (totalWords > 0) ? ((double) totalCorrect / totalWords) * 100 : 0.0;

        // Tính số từ cần review vào ngày mai
        Integer reviewTomorrowCount = null;
        if (date.isEqual(LocalDate.now())) {
            List<UserWord> tomorrowWords = userWordRepository.findByUserAndNextReviewDate(user, LocalDate.now().plusDays(1));
            reviewTomorrowCount = tomorrowWords.size();
        }

        return DailyStatsDTO.builder()
                .date(date)
                .totalWordsStudied(totalWords)
                .totalCorrect(totalCorrect)
                .totalIncorrect(totalWords - totalCorrect)
                .accuracy(accuracy)
                .sessionCount(sessions.size())
                .reviewTomorrow(reviewTomorrowCount)
                .sessions(sessionSummaries)
                .build();
    }

    @Transactional(readOnly = true)
    public HomeStatsDTO getHomeStatistics(User user) {
        long totalWords = userWordRepository.countByUser(user);
        long totalCollections = collectionRepository.countByUserAndIsDeletedFalse(user);
        long todayWords = userWordRepository.countByUserAndNextReviewDateLessThanEqual(user, LocalDate.now());

        Integer learningStreak = learningStreakRepository.findById(user.getUserId())
                .map(streak -> {
                    if (streak.isStreakActive()) {
                        return streak.getCurrentStreakDays();
                    } else {
                        return 0;
                    }
                })
                .orElse(0);

        return HomeStatsDTO.builder()
                .totalWords(totalWords)
                .totalCollections(totalCollections)
                .todayWords(todayWords)
                .learningStreak(learningStreak)
                .build();
    }

    @Transactional(readOnly = true)
    public GeneralStatsResponse getGeneralStatistics(User user, GeneralStatsRequest request) {
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().plusDays(1).atStartOfDay();
        // 1. Overview
        long totalWordsLearned = userWordRepository.countByUser(user);
        long wordsReviewedToday = learningSessionRepository.countByUserAndSessionDateAfter(user, LocalDate.now().atStartOfDay());
        // Retention rate là một logic phức tạp, tạm thời để giá trị giả
        double retentionRate = 0.75;

        GeneralStatsResponse.Overview overview = GeneralStatsResponse.Overview.builder()
                .totalWordsLearned(totalWordsLearned)
                .wordsReviewedToday(wordsReviewedToday)
                .retentionRate(retentionRate)
                .build();

        // 2. Learning Trend (ví dụ cho tuần)
        List<LearningSession> recentSessions = learningSessionRepository.findByUserAndSessionDateBetween(user, startDateTime, endDateTime);
        List<GeneralStatsResponse.TrendPoint> learningTrend = recentSessions.stream()
                .filter(s -> s.getStatus() == LearningSession.SessionStatus.COMPLETED) // Chỉ tính các session đã hoàn thành
                .collect(Collectors.groupingBy(
                        s -> s.getSessionDate().toLocalDate(),
                        Collectors.averagingDouble(s -> s.getCorrectCount() != null ? s.getCorrectCount() : 0) // Xử lý null
                ))
                .entrySet().stream()
                .map(entry -> GeneralStatsResponse.TrendPoint.builder()
                        .day(entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                        .score(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        // 3. Most Forgotten Words
        List<UserWord> forgottenUserWords = userWordRepository.findWordsForExtensionPractice(user.getUserId(), PageRequest.of(0, 5));
        List<GeneralStatsResponse.ForgottenWord> mostForgottenWords = forgottenUserWords.stream()
                .map(uw -> GeneralStatsResponse.ForgottenWord.builder()
                        .word(uw.getWord().getWordText())
                        .partOfSpeech(uw.getWord().getPartOfSpeech())
                        .build())
                .collect(Collectors.toList());

        // 4. Pronunciation Stats
        List<PronunciationRecord> pronunciationRecords = pronunciationRecordRepository.findByUserAndSessionDateBetween(user, startDateTime, endDateTime);
        double avgPronunciationAccuracy = pronunciationRecords.stream()
                .filter(r -> r.getScore() != null) // Lọc bỏ các bản ghi có score là null
                .mapToDouble(PronunciationRecord::getScore)
                .average()
                .orElse(0.0);

        List<GeneralStatsResponse.TrendPoint> pronunciationTrend = pronunciationRecords.stream()
                .filter(r -> r.getScore() != null) // Lọc bỏ các bản ghi có score là null
                .collect(Collectors.groupingBy(
                        r -> r.getSessionDate().toLocalDate(),
                        Collectors.averagingDouble(PronunciationRecord::getScore)
                ))
                .entrySet().stream()
                .map(entry -> GeneralStatsResponse.TrendPoint.builder()
                        .day(entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                        .score(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        GeneralStatsResponse.PronunciationStats pronunciationStats = GeneralStatsResponse.PronunciationStats.builder()
                .accuracy(avgPronunciationAccuracy)
                .improvementTrend(pronunciationTrend)
                .build();

        return GeneralStatsResponse.builder()
                .userId(user.getUserId())
                .overview(overview)
                .learningTrend(learningTrend)
                .mostForgottenWords(mostForgottenWords)
                .pronunciation(pronunciationStats)
                .build();
    }

    @Transactional(readOnly = true)
    public CollectionProgressDTO getCollectionProgress(User user, String collectionName) {
        // 1. Tìm collection
        Collection collection = collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user)
                .orElseThrow(() -> new EntityNotFoundException("Collection '" + collectionName + "' not found."));

        // 2. Lấy tất cả các từ của user trong collection này
        List<UserWord> userWords = userWordRepository.findByUserAndCollection_CollectionId(user, collection.getCollectionId());

        if (userWords.isEmpty()) {
            // Trả về dữ liệu rỗng nếu collection chưa có từ nào
            return CollectionProgressDTO.builder().totalWords(0).averageScore(0).build();
        }

        // 3. Phân loại các từ theo 6 cấp độ
        long level1 = 0; // score < 0.2 (Very Weak)
        long level2 = 0; // 0.2 <= score < 0.4 (Weak)
        long level3 = 0; // 0.4 <= score < 0.6 (Learning)
        long level4 = 0; // 0.6 <= score < 0.8 (Good)
        long level5 = 0; // 0.8 <= score < 0.95 (Strong)
        long level6 = 0; // score >= 0.95 (Mastered)
        double totalScore = 0;

        for (UserWord uw : userWords) {
            float score = uw.getFamiliarityScore();
            totalScore += score;
            if (score < 0.2) level1++;
            else if (score < 0.4) level2++;
            else if (score < 0.6) level3++;
            else if (score < 0.8) level4++;
            else if (score < 0.95) level5++;
            else level6++;
        }

        // 4. Xây dựng dữ liệu cho biểu đồ
        List<String> labels = List.of("Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Mastered");
        List<CollectionProgressDTO.LevelData> data = List.of(
                CollectionProgressDTO.LevelData.builder().levelName("Level 1").wordCount(level1).build(),
                CollectionProgressDTO.LevelData.builder().levelName("Level 2").wordCount(level2).build(),
                CollectionProgressDTO.LevelData.builder().levelName("Level 3").wordCount(level3).build(),
                CollectionProgressDTO.LevelData.builder().levelName("Level 4").wordCount(level4).build(),
                CollectionProgressDTO.LevelData.builder().levelName("Level 5").wordCount(level5).build(),
                CollectionProgressDTO.LevelData.builder().levelName("Mastered").wordCount(level6).build()
        );

        CollectionProgressDTO.ChartData chartData = CollectionProgressDTO.ChartData.builder()
                .labels(labels)
                .data(data)
                .build();

        // 5. Tìm ngày review cuối cùng
        LocalDate lastReviewedOn = userWords.stream()
                .map(UserWord::getNextReviewDate)
                .min(LocalDate::compareTo) // Tìm ngày review gần nhất trong quá khứ/tương lai
                .orElse(null);

        return CollectionProgressDTO.builder()
                .progressChart(chartData)
                .lastReviewedOn(lastReviewedOn)
                .totalWords(userWords.size())
                .averageScore(totalScore / userWords.size())
                .build();
    }

    @Transactional(readOnly = true)
    public CoreHomeStatsDTO getCoreHomeStatistics(User user) {

        // 1. Tổng số từ hiện có (Total Words)
        long totalWords = userWordRepository.countByUser(user);

        // 2. Tổng số collection (Chỉ đếm collection chưa bị xóa)
        long totalCollections = collectionRepository.countByUserAndIsDeletedFalse(user);

        // 3. Tổng số từ cần học hôm nay (Today Words)
        long todayWords = userWordRepository.countByUserAndNextReviewDateLessThanEqual(user, LocalDate.now());

        // 4. Điểm trung bình phát âm
        Double avgScore = pronunciationRecordRepository.findAverageScoreByUser(user)
                .orElse(0.0);

        return CoreHomeStatsDTO.builder()
                .totalWords(totalWords)
                .totalCollections(totalCollections)
                .todayWords(todayWords)
                .avgPronunciationScore(Math.round(avgScore * 100.0) / 100.0)
                .build();
    }
}