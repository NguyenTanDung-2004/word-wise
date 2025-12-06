package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.ChallengeRoom;
import com.example.core_word_wise.entity.ChallengeRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChallengeRoomParticipantRepository extends JpaRepository<ChallengeRoomParticipant, Integer> {
    List<ChallengeRoomParticipant> findAllByRoom(ChallengeRoom room);
    List<ChallengeRoomParticipant> findAllByRoom_RoomId(Integer roomId);
}