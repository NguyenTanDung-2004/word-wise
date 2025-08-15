package com.example.WordWise.service;

import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.entity.Word;
import com.example.WordWise.enums.PromptEnum;
import com.example.WordWise.utils.APIUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;

@Service
public class ChatbotService {
    @Autowired
    private WordService wordService;

    @Autowired
    private APIUtils apiUtils;

    @Value("${external.gemini-api-json}")
    private String geminiAPIJsonPath;

    @Value("${external.gemini-url}")
    private String geminiURL;

    public Word addWordAutomatically(String userId, String prompt) {
        String enumPromptValue = PromptEnum.ADD_WORD_AUTO.getValue();
        enumPromptValue = enumPromptValue.replaceAll("<userId>", userId);
        enumPromptValue = enumPromptValue.replaceAll("<description>", prompt);

        String escapedPrompt = apiUtils.escape(enumPromptValue);

        String jsonContent = "";
        try {
            jsonContent = this.apiUtils.loadJsonConfig(geminiAPIJsonPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonContent = this.apiUtils.replaceValue(Arrays.asList("${replaced}"), Arrays.asList(escapedPrompt), jsonContent);

        ResponseEntity<String> response = this.apiUtils.callApi(
                new HashMap<>(), jsonContent, geminiURL, HttpMethod.POST
        );

        String bodyResponse = response.getBody();
        if (bodyResponse == null) {
            throw new RuntimeException("API response body is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(bodyResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

// Extract "text" content
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

// Remove triple backticks and "json" label if present
        text = text.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

// Deserialize to InsertWordRequest
        InsertWordRequest requestObj = null;
        try {
            requestObj = mapper.readValue(text, InsertWordRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return wordService.insertWord(requestObj);
    }
}
