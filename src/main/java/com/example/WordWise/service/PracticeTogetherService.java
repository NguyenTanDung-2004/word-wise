package com.example.WordWise.service;

import com.example.WordWise.dto.request.RequestCreatePracticeTogetherRoom;
import com.example.WordWise.entity.PracticeRoomQuestion;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.entity.User;
import com.example.WordWise.enums.KafkaTopics;
import com.example.WordWise.enums.PromptEnum;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.model.KafkaObjects.CreatePracticeRoomKafkaObject;
import com.example.WordWise.model.redis_object.CreatePracticeRoomRedisObject;
import com.example.WordWise.repository.PracticeRoomQuestionRepository;
import com.example.WordWise.repository.PracticeTogetherRoomRepository;
import com.example.WordWise.utils.APIUtils;
import com.example.WordWise.utils.JsonUtils;
import com.example.WordWise.utils.KafkaUtils;
import com.example.WordWise.utils.RedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    @Value("${external.gemini-api-json}")
    private String geminiAPIJsonPath;

    @Value("${external.gemini-url}")
    private String geminiURL;

    @Autowired
    private APIUtils apiUtils;

    @Autowired
    private PracticeRoomQuestionRepository practiceRoomQuestionRepository;

    @Autowired
    private RedisUtils redisUtils;

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
                .subjects(room.getSubjects())
                .build();

        String jsonKafkaObject = "";
        try {
            jsonKafkaObject = jsonUtils.toJson(kafkaObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaUtils.sendMessageToKafka(KafkaTopics.CREATE_PRACTICE_ROOM.getKey(), jsonKafkaObject);

        // add data to redis
        CreatePracticeRoomRedisObject redisObject = CreatePracticeRoomRedisObject.builder()
                .roomId(room.getId())
                .numberOfQuestions(room.getNumberOfQuestions())
                .userId(room.getUser().getUserId())
                .subjects(room.getSubjects())
                .build();
        redisUtils.set("admin/" + room.getId(), redisObject, 0);

        return room;
    }

    public void generateQuestions(String message) {
        message = kafkaUtils.replaceSpecificCharacters(message);
        // get room from message
        CreatePracticeRoomKafkaObject roomRequest = null;
        try {
            roomRequest = (CreatePracticeRoomKafkaObject) jsonUtils.toObject(message, CreatePracticeRoomKafkaObject.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String prompt = PromptEnum.GEN_QUESTIONS_PRACTICE_ROOM.getValue();
        prompt = prompt.replaceAll("<topics>", roomRequest.getSubjects());
        prompt = prompt.replaceAll("<total>", roomRequest.getNumberOfQuestions().toString());
        prompt = this.apiUtils.escape(prompt);

        String jsonContent = "";
        try {
            jsonContent = this.apiUtils.loadJsonConfig(geminiAPIJsonPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonContent = this.apiUtils.replaceValue(Arrays.asList("${replaced}"), Arrays.asList(prompt), jsonContent);

        ResponseEntity<String> response = this.apiUtils.callApi(
                new HashMap<>(), jsonContent, geminiURL, HttpMethod.POST
        );

        String bodyResponse = response.getBody();
        if (bodyResponse == null) {
            throw new RuntimeException("API response body is null");
        }

        String text = "";
        try {
            text = jsonUtils.getTextFromGeminiResponse(bodyResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (text.startsWith("```json")) {
            text = text.substring(7); // remove leading ```json
        }
        text = text.replaceAll("^\\s*```", ""); // remove any remaining leading ```
        text = text.replaceAll("```\\s*$", ""); // remove trailing ```
        text = text.trim();

        List<PracticeRoomQuestion> list = new ArrayList<>();
        PracticeTogetherRoom practiceTogetherRoom = practiceTogetherRoomRepository.findById(roomRequest.getRoomId()).get();
        try {
            list = jsonUtils.jsonToPracticeRoomQuestion(text, practiceTogetherRoom);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // save
        practiceRoomQuestionRepository.saveAll(list);

        // push event to kafka (topic: room is ready)
    }
}
