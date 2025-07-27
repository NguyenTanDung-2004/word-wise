package com.example.WordWise.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpHeaders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class APIUtils {
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity callApi(Map<String, String> headerValues, String jsonBody, String url, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, String> entry : headerValues.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
        }

        // Create request
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(url, method,
                requestEntity, String.class);

        return response;
    }

    public String loadJsonConfig(String path) throws Exception{
        // Load config
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public String replaceValue(List<String> replacedField, List<Object> data, String content) {
        for (int i = 0; i < replacedField.size(); i++) {
            content = content.replace(replacedField.get(i), data.get(i).toString());
        }

        return content;
    }

    public String escape(String input) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false); // ✅ allow UTF-8
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // optional, just for pretty print

        String json = null; // e.g., class with "text" field
        try {
            json = objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json;
    }


}
