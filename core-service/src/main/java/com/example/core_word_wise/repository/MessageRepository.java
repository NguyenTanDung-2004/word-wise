package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation_ConversationIdOrderByTimestampAsc(Integer conversationId);
}