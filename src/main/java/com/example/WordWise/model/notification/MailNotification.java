package com.example.WordWise.model.notification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.WordWise.enums.EmailTypeEnum;
import com.example.WordWise.exception.UserException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Component
public class MailNotification extends Notification {
    private String subject;
    private EmailTypeEnum emailType;
    private String code;

    public void setDataResetPassword(String to, String code, EmailTypeEnum emailType) {
        switch (emailType) {
            case RESET_PASSWORD:
                this.subject = "Request Reset Password";
                break;
        
            default:
                break;
        }
        this.setTo(to);
        this.setCode(code);
        this.setEmailType(emailType);
    }
}
