package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.LearningStreak;
import jakarta.persistence.LockModeType; // <-- Import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock; // <-- Import
import org.springframework.data.jpa.repository.Query; // <-- Import

import java.util.Optional;

public interface LearningStreakRepository extends JpaRepository<LearningStreak, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ls FROM LearningStreak ls WHERE ls.userId = :userId")
    Optional<LearningStreak> findByIdForUpdate(Integer userId);
}