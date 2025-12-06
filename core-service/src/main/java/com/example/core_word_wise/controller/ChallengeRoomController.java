package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.challenge.CreateRoomRequest;
import com.example.core_word_wise.dto.challenge.InviteFriendsRequest;
import com.example.core_word_wise.dto.challenge.JoinRoomRequest;
import com.example.core_word_wise.dto.challenge.RoomResponse;
import com.example.core_word_wise.dto.user.UserResponse;
import com.example.core_word_wise.entity.ChallengeRoom;
import com.example.core_word_wise.entity.ChallengeRoomParticipant;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.ChallengeRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/challenge-rooms")
@RequiredArgsConstructor
public class ChallengeRoomController {

    private final ChallengeRoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @AuthenticationPrincipal User host,
            @Valid @RequestBody CreateRoomRequest request
    ) {
        ChallengeRoom room = roomService.createRoom(host, request);
        return new ResponseEntity<>(ApiResponse.success(mapToRoomResponse(room), "Room created successfully."), HttpStatus.CREATED);
    }

    @PostMapping("/{roomId}/invite")
    public ResponseEntity<ApiResponse<Void>> inviteFriends(
            @AuthenticationPrincipal User host,
            @PathVariable Integer roomId,
            @Valid @RequestBody InviteFriendsRequest request
    ) {
        roomService.inviteFriends(host, roomId, request.getFriendIds());
        return ResponseEntity.ok(ApiResponse.success(null, "Invitations sent."));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<RoomResponse>> joinRoom(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JoinRoomRequest request
    ) {
        ChallengeRoom room = roomService.joinRoom(user, request.getInviteCode());
        return ResponseEntity.ok(ApiResponse.success(mapToRoomResponse(room), "Joined room successfully."));
    }

    private RoomResponse mapToRoomResponse(ChallengeRoom room) {
        List<ChallengeRoomParticipant> participants = roomService.getParticipants(room.getRoomId());
        List<UserResponse> participantDtos = participants.stream()
                .map(p -> UserResponse.builder()
                        .userId(p.getUser().getUserId())
                        .username(p.getUser().getDisplayName())
                        .avatarUrl(p.getUser().getAvatarUrl())
                        .build())
                .collect(Collectors.toList());

        return RoomResponse.builder()
                .roomId(room.getRoomId())
                .inviteCode(room.getInviteCode())
                .gameMode(room.getGameMode().getName())
                .status(room.getStatus().name())
                .host(participantDtos.stream().filter(p -> p.getUserId().equals(room.getHost().getUserId())).findFirst().orElse(null))
                .participants(participantDtos)
                .build();
    }
}