package com.example.core_word_wise.service;

import com.example.core_word_wise.entity.LearningStreak;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.LearningStreakRepository;
import com.example.core_word_wise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final LearningStreakRepository learningStreakRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LearningStreak updateLearningStreak(Integer userId) {
        Optional<LearningStreak> streakOpt = learningStreakRepository.findByIdForUpdate(userId);

        LearningStreak streak;
        if (streakOpt.isPresent()) {
            streak = streakOpt.get();
        } else {
            try {
                User managedUser = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("User not found during streak creation."));

                LearningStreak newStreak = new LearningStreak();
                newStreak.setUserId(managedUser.getUserId());
                newStreak.setUser(managedUser);
                newStreak.setCurrentStreakDays(0);
                newStreak.setLongestStreakDays(0);
                streak = learningStreakRepository.saveAndFlush(newStreak);
            } catch (DataIntegrityViolationException e) {
                streak = learningStreakRepository.findByIdForUpdate(userId)
                        .orElseThrow(() -> new IllegalStateException("Failed to fetch streak after race condition for user: " + userId));
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate lastStudy = streak.getLastStudyDate();

        if (lastStudy == null || !lastStudy.isEqual(today)) {
            if (lastStudy != null && ChronoUnit.DAYS.between(lastStudy, today) == 1) {
                streak.setCurrentStreakDays(streak.getCurrentStreakDays() + 1);
            } else {
                streak.setCurrentStreakDays(1);
            }
        }

        if (streak.getCurrentStreakDays() > streak.getLongestStreakDays()) {
            streak.setLongestStreakDays(streak.getCurrentStreakDays());
        }
        streak.setLastStudyDate(today);
        return learningStreakRepository.save(streak);
    }
}