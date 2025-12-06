package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.challenge.CreateRoomRequest;
import com.example.core_word_wise.dto.challenge.RoomResponse;
import com.example.core_word_wise.dto.user.UserResponse;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeRoomService {

    private final ChallengeRoomRepository roomRepository;
    private final ChallengeRoomParticipantRepository participantRepository;
    private final GameModeRepository gameModeRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Transactional
    public ChallengeRoom createRoom(User host, CreateRoomRequest request) {
        GameMode gameMode = gameModeRepository.findByName(request.getGameMode())
                .orElseThrow(() -> new EntityNotFoundException("Game mode not found."));

        ChallengeRoom room = new ChallengeRoom();
        room.setHost(host);
        room.setGameMode(gameMode);
        room.setInviteCode(RandomStringUtils.randomAlphanumeric(6).toUpperCase());
        ChallengeRoom savedRoom = roomRepository.save(room);

        ChallengeRoomParticipant hostParticipant = new ChallengeRoomParticipant();
        hostParticipant.setRoom(savedRoom);
        hostParticipant.setUser(host);
        participantRepository.save(hostParticipant);

        return savedRoom;
    }

    public void inviteFriends(User host, Integer roomId, List<Integer> friendIds) {
        ChallengeRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found."));

        if (!room.getHost().getUserId().equals(host.getUserId())) {
            throw new SecurityException("Only the host can invite friends.");
        }

        friendIds.forEach(friendId -> {
            String destination = "/queue/notifications";
            Map<String, Object> payload = Map.of(
                    "type", "GAME_INVITE",
                    "roomId", room.getRoomId(),
                    "inviteCode", room.getInviteCode(),
                    "hostName", host.getDisplayName(),
                    "gameMode", room.getGameMode().getName()
            );
            messagingTemplate.convertAndSendToUser(friendId.toString(), destination, payload);
        });
    }

    @Transactional
    public ChallengeRoom joinRoom(User user, String inviteCode) {
        ChallengeRoom room = roomRepository.findByInviteCodeAndStatus(inviteCode, ChallengeRoom.RoomStatus.WAITING)
                .orElseThrow(() -> new EntityNotFoundException("Room not found or is already in progress."));

        ChallengeRoomParticipant participant = new ChallengeRoomParticipant();
        participant.setRoom(room);
        participant.setUser(user);
        participantRepository.save(participant);

        List<User> participants = participantRepository.findAllByRoom(room).stream()
                .map(ChallengeRoomParticipant::getUser).toList();

        String destination = "/topic/challenge-room/" + room.getRoomId();
        messagingTemplate.convertAndSend(destination, Map.of("type", "PLAYER_JOINED", "participants", participants));

        return room;
    }

    public List<ChallengeRoomParticipant> getParticipants(Integer roomId) {
        return participantRepository.findAllByRoom_RoomId(roomId);
    }
}