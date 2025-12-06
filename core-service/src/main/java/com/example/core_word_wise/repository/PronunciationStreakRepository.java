package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.PronunciationStreak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PronunciationStreakRepository extends JpaRepository<PronunciationStreak, Integer> {
}