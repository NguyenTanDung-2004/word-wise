package com.example.WordWise.model.notification;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.WordWise.enums.EmailTypeEnum;
@Component
public class MailNotificationProcessor implements NotificationProcessor {
    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();

    private String createJsonBodyForgotPassword(String code) {
        String htmlContent;
        try {
            htmlContent = getTemplateJsonBody("ForgotPassword.txt");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        htmlContent = htmlContent.replace("{code}", code);
        
        return htmlContent;
    }

    @Override
    public void processNotification(Notification notification) {
        MailNotification mailNotification = (MailNotification) notification;
        
        switch (mailNotification.getEmailType()) {
            case RESET_PASSWORD:
                sendCodeResetPassword(mailNotification);
                break;
        
            default:
                break;
        }
    }

    private void sendCodeResetPassword(MailNotification mailNotification) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("content-type", "application/json");
        headers.set("api-key", env.getProperty("mail.apikey"));

        // set body
        String jsonBody = null;
        try {
            jsonBody = createJsonBody(mailNotification.getTo(), 
                    createJsonBodyForgotPassword(mailNotification.getCode()), 
                    mailNotification.getSubject());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Create request
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(env.getProperty("mail.url"), HttpMethod.POST,
                requestEntity, String.class);
    }

    private String getTemplateJsonBody(String fileName) throws IOException {
        String templatePath = env.getProperty("mail.templatepath");
        String fullPath = templatePath + "/" + fileName;

        ClassPathResource resource = new ClassPathResource(fullPath);
        InputStream inputStream = resource.getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }


    private String createJsonBody(String to, String message, String subject) throws IOException {
        String template = getTemplateJsonBody("JsonForgotPasswordFormat.txt");

        Map<String, String> datas = new LinkedHashMap<>();
        datas.put("sendername", env.getProperty("mail.sendername"));
        datas.put("senderemail", env.getProperty("mail.senderemail"));
        datas.put("recipientemail", to);
        datas.put("htmlcontent", message);
        datas.put("subject", subject);

        // set data
        for (Map.Entry<String, String> entry : datas.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }
}


