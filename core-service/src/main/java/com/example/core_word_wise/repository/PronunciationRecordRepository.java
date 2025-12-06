package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.PronunciationRecord;
import com.example.core_word_wise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PronunciationRecordRepository extends JpaRepository<PronunciationRecord, Integer> {

    List<PronunciationRecord> findByUserAndSessionDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT AVG(pr.score) FROM PronunciationRecord pr WHERE pr.user = :user")
    Optional<Double> findAverageScoreByUser(User user);
}