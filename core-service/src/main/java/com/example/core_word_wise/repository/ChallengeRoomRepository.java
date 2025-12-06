package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.ChallengeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChallengeRoomRepository extends JpaRepository<ChallengeRoom, Integer> {
    Optional<ChallengeRoom> findByInviteCodeAndStatus(String inviteCode, ChallengeRoom.RoomStatus status);
}