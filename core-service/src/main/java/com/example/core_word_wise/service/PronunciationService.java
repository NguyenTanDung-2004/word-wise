package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.pronunciation.SavePronunciationRequest;
import com.example.core_word_wise.dto.pronunciation.SavePronunciationResponse;
import com.example.core_word_wise.entity.PronunciationRecord;
import com.example.core_word_wise.entity.PronunciationStreak;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.PronunciationRecordRepository;
import com.example.core_word_wise.repository.PronunciationStreakRepository;
import com.example.core_word_wise.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PronunciationService {

    private final PronunciationRecordRepository recordRepository;
    private final PronunciationStreakRepository streakRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Transactional
    public SavePronunciationResponse savePronunciationResult(User detachedUser, SavePronunciationRequest request) {
        User managedUser = userRepository.findById(detachedUser.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found during pronunciation save."));

        LocalDateTime now = LocalDateTime.now();
        List<PronunciationRecord> records = new ArrayList<>();
        for (SavePronunciationRequest.Result result : request.getResults()) {
            PronunciationRecord record = new PronunciationRecord();
            record.setUser(managedUser); // <-- SỬ DỤNG ĐỐI TƯỢNG MANAGED
            record.setSentenceId(result.getSentenceId());
            record.setUserAudioUrl(result.getUserAudioUrl());
            record.setScore((float) result.getScore());
            record.setSessionDate(now);
            try {
                record.setFeedbackOverall(result.getFeedback().getOverall());
                record.setProblemSounds(objectMapper.writeValueAsString(result.getFeedback().getProblemSounds()));
                record.setMissedWords(objectMapper.writeValueAsString(result.getFeedback().getMissedWords()));
            } catch (Exception e) { /* Bỏ qua */ }
            records.add(record);
        }
        recordRepository.saveAll(records);

        PronunciationStreak streak = updatePronunciationStreak(managedUser);

        int passedCount = (int) request.getResults().stream().filter(r -> r.getScore() >= 50).count();
        int failedCount = request.getResults().size() - passedCount;

        SavePronunciationResponse.StreakInfo streakInfo = SavePronunciationResponse.StreakInfo.builder()
                .currentStreakDays(streak.getCurrentStreakDays())
                .longestStreakDays(streak.getLongestStreakDays())
                .build();

        SavePronunciationResponse.SessionSummaryResponse summaryResponse = SavePronunciationResponse.SessionSummaryResponse.builder()
                .totalSentences(request.getSummary().getTotalSentences())
                .completed(request.getSummary().getCompleted())
                .avgScore(request.getSummary().getAvgScore())
                .passedSentences(passedCount)
                .failedSentences(failedCount)
                .streakInfo(streakInfo)
                .build();

        return SavePronunciationResponse.builder()
                .status("success")
                .message("Pronunciation results saved successfully.")
                .summary(summaryResponse)
                .results(request.getResults())
                .build();
    }

    private PronunciationStreak updatePronunciationStreak(User user) {
        PronunciationStreak streak = streakRepository.findById(user.getUserId()).orElseGet(() -> {
            PronunciationStreak newStreak = new PronunciationStreak();
            newStreak.setUserId(user.getUserId());
            newStreak.setUser(user);
            newStreak.setCurrentStreakDays(0);
            newStreak.setLongestStreakDays(0);
            return newStreak;
        });

        LocalDate today = LocalDate.now();
        LocalDate lastPractice = streak.getLastPracticeDate();

        if (lastPractice == null || !lastPractice.isEqual(today)) {
            if (lastPractice != null && ChronoUnit.DAYS.between(lastPractice, today) == 1) {
                streak.setCurrentStreakDays(streak.getCurrentStreakDays() + 1);
            } else {
                streak.setCurrentStreakDays(1);
            }
        }

        if (streak.getCurrentStreakDays() > streak.getLongestStreakDays()) {
            streak.setLongestStreakDays(streak.getCurrentStreakDays());
        }
        streak.setLastPracticeDate(today);
        return streakRepository.save(streak);
    }
}