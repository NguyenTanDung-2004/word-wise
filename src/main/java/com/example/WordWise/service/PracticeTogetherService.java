package com.example.WordWise.service;

import com.example.WordWise.dto.request.RequestCreatePracticeTogetherRoom;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.entity.User;
import com.example.WordWise.enums.KafkaTopics;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.model.KafkaObjects.CreatePracticeRoomKafkaObject;
import com.example.WordWise.repository.PracticeTogetherRoomRepository;
import com.example.WordWise.utils.JsonUtils;
import com.example.WordWise.utils.KafkaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PracticeTogetherService {

    @Autowired
    @Qualifier("practiceTogetherRoomMapper")
    private Mapper mapper;

    @Autowired
    private PracticeTogetherRoomRepository practiceTogetherRoomRepository;

    @Autowired
    private KafkaUtils kafkaUtils;

    @Autowired
    private JsonUtils jsonUtils;

    public void requestJoin(String roomId, String userId) {
        // public event to kafka
    }

    @Transactional(rollbackOn = Exception.class)
    public PracticeTogetherRoom createRoom(User user, RequestCreatePracticeTogetherRoom request) {
        PracticeTogetherRoom room = new PracticeTogetherRoom();
        room = (PracticeTogetherRoom) mapper.map(request, room);
        room.setUser(user);
        room = practiceTogetherRoomRepository.save(room);

        // push event to kafka to generate question
        CreatePracticeRoomKafkaObject kafkaObject = CreatePracticeRoomKafkaObject.builder()
                .roomId(room.getId())
                .numberOfQuestions(room.getNumberOfQuestions())
                .userId(room.getUser().getUserId())
                .build();

        String jsonKafkaObject = "";
        try {
            jsonKafkaObject = jsonUtils.toJson(kafkaObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaUtils.sendMessageToKafka(KafkaTopics.CREATE_PRACTICE_ROOM.getKey(), jsonKafkaObject);
        return room;
    }
}
