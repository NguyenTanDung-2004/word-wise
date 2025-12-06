package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.chat.ConversationDetailResponse;
import com.example.core_word_wise.dto.chat.MessageResponse;
import com.example.core_word_wise.dto.chat.TopicChatResponse;
import com.example.core_word_wise.dto.user.UserResponse;
import com.example.core_word_wise.entity.Conversation;
import com.example.core_word_wise.entity.Message;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.ConversationRepository;
import com.example.core_word_wise.repository.MessageRepository;
import com.example.core_word_wise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<TopicChatResponse> getChatTopics(User currentUser) {
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(currentUser.getUserId());

        return conversations.stream().map(conv -> {
            User otherUser = conv.getParticipants().stream()
                    .filter(p -> !p.getUserId().equals(currentUser.getUserId()))
                    .findFirst()
                    .orElse(null);

            if (otherUser == null) return null;

            Message lastMessage = conv.getMessages().isEmpty() ? null : conv.getMessages().get(conv.getMessages().size() - 1);

            return TopicChatResponse.builder()
                    .conversationId(conv.getConversationId())
                    .otherUserId(otherUser.getUserId())
                    .name(otherUser.getDisplayName())
                    .avatar(otherUser.getAvatarUrl())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet.")
                    .time(lastMessage != null ? lastMessage.getTimestamp() : conv.getCreatedAt())
                    .unreadCount(0)
                    .isOnline(false)
                    .build();
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional
    public ConversationDetailResponse getConversationDetails(User currentUser, Integer conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found."));

        boolean isParticipant = conversation.getParticipants().stream().anyMatch(p -> p.getUserId().equals(currentUser.getUserId()));
        if (!isParticipant) {
            throw new SecurityException("User is not a participant of this conversation.");
        }

        List<MessageResponse> messages = conversation.getMessages().stream()
                .map(msg -> MessageResponse.builder()
                        .messageId(msg.getMessageId())
                        .senderId(msg.getSender().getUserId())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .isSender(msg.getSender().getUserId().equals(currentUser.getUserId()))
                        .build())
                .collect(Collectors.toList());

        List<UserResponse> participants = conversation.getParticipants().stream()
                .map(p -> UserResponse.builder().userId(p.getUserId()).username(p.getDisplayName()).avatarUrl(p.getAvatarUrl()).build())
                .collect(Collectors.toList());

        return ConversationDetailResponse.builder()
                .conversationId(conversation.getConversationId())
                .participants(participants)
                .messages(messages)
                .build();
    }

    @Transactional
    public MessageResponse sendMessage(User sender, Integer receiverId, String content) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found."));

        Conversation conversation = conversationRepository.findConversationBetweenUsers(sender.getUserId(), receiverId)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setParticipants(Arrays.asList(sender, receiver));
                    return conversationRepository.save(newConv);
                });

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse.builder()
                .messageId(savedMessage.getMessageId())
                .senderId(sender.getUserId())
                .content(content)
                .timestamp(savedMessage.getTimestamp())
                .build();

        String destination = "/topic/conversation/" + conversation.getConversationId();
        messagingTemplate.convertAndSend(destination, messageResponse);

        messageResponse.setSender(true);
        return messageResponse;
    }
}