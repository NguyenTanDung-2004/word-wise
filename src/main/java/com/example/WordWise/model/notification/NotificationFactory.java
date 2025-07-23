package com.example.WordWise.model.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class NotificationFactory {
    @Autowired
    private MailNotification mailNotification;
    public Notification createNotification(Class<? extends Notification> notificationClass) {
        if (notificationClass == MailNotification.class) {
            return mailNotification;
        }
       
        return null;
    }
}
