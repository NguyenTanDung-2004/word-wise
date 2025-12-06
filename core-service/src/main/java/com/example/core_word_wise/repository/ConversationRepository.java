package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    // Tìm conversation giữa 2 người
    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 WHERE p1.id = :userId1 AND p2.id = :userId2")
    Optional<Conversation> findConversationBetweenUsers(Integer userId1, Integer userId2);

    // Lấy tất cả conversation của 1 người
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId")
    List<Conversation> findConversationsByUserId(Integer userId);
}