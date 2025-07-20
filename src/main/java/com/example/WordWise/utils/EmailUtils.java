package com.example.WordWise.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.WordWise.model.notification.Notification;


@Component
public class EmailUtils {

    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(Notification notification) throws IOException {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("content-type", "application/json");
        headers.set("api-key", env.getProperty("mail.apikey"));

        // set body
        String jsonBody = createJsonBody(notification);

        // Create request
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(env.getProperty("mail.url"), HttpMethod.POST,
                requestEntity, String.class);
    }

    private String getTemplateJsonBody() throws IOException {
        // create absolute path
        String absolutePath = Paths.get(env.getProperty("mail.templatepath")).toAbsolutePath().toString();

        // read file
        Path filePath = Paths.get(absolutePath, "JsonBodyTemplate.txt");
        return Files.readString(filePath);
    }

    private Map<String, String> getDatas(Notification notification) {
        // casting notification
        MailNotification mailNotification = (MailNotification) notification;

        Map<String, String> datas = new LinkedHashMap<>();
        datas.put("sendername", env.getProperty("mail.sendername"));
        datas.put("senderemail", env.getProperty("mail.senderemail"));
        datas.put("recipientemail", mailNotification.getTo());
        datas.put("htmlcontent", mailNotification.getBody());
        datas.put("subject", mailNotification.getSubject());

        return datas;
    }

    private String createJsonBody(Notification notification) throws IOException {
        String template = getTemplateJsonBody();
        Map<String, String> datas = getDatas(notification);

        // set data
        for (Map.Entry<String, String> entry : datas.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }
}
