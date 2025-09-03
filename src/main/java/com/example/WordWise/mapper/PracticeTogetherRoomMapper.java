package com.example.WordWise.mapper;

import com.example.WordWise.dto.request.RequestCreatePracticeTogetherRoom;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.enums.PracticeTogetherRoomStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("practiceTogetherRoomMapper")
public class PracticeTogetherRoomMapper implements Mapper{
    @Override
    public Object map(Object source, Object target) {
        if (source instanceof RequestCreatePracticeTogetherRoom &&
                target instanceof PracticeTogetherRoom) {
            return map((RequestCreatePracticeTogetherRoom) source);
        }

        return null;
    }

    private PracticeTogetherRoom map(RequestCreatePracticeTogetherRoom source) {
        return PracticeTogetherRoom.builder()
                .name(source.getName())
                .status(PracticeTogetherRoomStatus.PROCESSING.getKey())
                .password(source.getPassword())
                .numberOfQuestions(source.getNumberOfQuestions())
                .numberOfMembers(source.getNumberOfMembers())
                .roomCode(source.getRoomCode())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .isPublic(source.getIsPublic())
                .build();
    }
}
