package com.example.core_word_wise.entity;

import com.example.core_word_wise.entity.ids.ConversationParticipantId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conversation_participant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipant {
    @EmbeddedId
    private ConversationParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}