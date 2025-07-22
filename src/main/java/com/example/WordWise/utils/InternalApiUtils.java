package com.example.WordWise.utils;

import org.springframework.http.HttpHeaders;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InternalApiUtils {
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity callApi(Map<String, String> map, String jsonBody, String url, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, String> entry : map.entrySet()) { 
            headers.set(entry.getKey(), entry.getValue());
        }

        // Create request
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(url, method,
                requestEntity, String.class);

        return response;
    }


}
