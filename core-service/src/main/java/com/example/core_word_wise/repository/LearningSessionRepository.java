package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.LearningSession;
import com.example.core_word_wise.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LearningSessionRepository extends JpaRepository<LearningSession, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LearningSession> findBySessionUuidAndStatus(String sessionUuid, LearningSession.SessionStatus status);
    List<LearningSession> findByUserAndSessionDateBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    long countByUserAndSessionDateAfter(User user, LocalDateTime date);
}