package com.example.WordWise.utils;

import com.example.WordWise.entity.PracticeRoomQuestion;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonUtils {
    @Autowired
    private ObjectMapper objectMapper;

    public String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public Object toObject(String json, Class className) throws JsonProcessingException {
        return objectMapper.readValue(json, className);
    }

    public List<PracticeRoomQuestion> jsonToPracticeRoomQuestion(String json, PracticeTogetherRoom practiceTogetherRoom) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        List<PracticeRoomQuestion> questions = new ArrayList<>();

        for (JsonNode node : rootNode) {
            PracticeRoomQuestion q = new PracticeRoomQuestion();
            q.setRoom(practiceTogetherRoom);
            q.setCreatedDate(LocalDateTime.now());
            q.setType(node.path("type").asText());
            q.setQuestion(node.path("question").asText());
            q.setAnswer(node.path("anwser").asText()); // note: API typo

            // Convert options array to JSON string
            JsonNode optionsNode = node.path("options");
            q.setOptions(mapper.writeValueAsString(optionsNode));

            questions.add(q);
        }

        return questions;
    }

    public String getTextFromGeminiResponse(String bodyResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(bodyResponse);

        // Get the "text" content
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        return text;
    }

}
