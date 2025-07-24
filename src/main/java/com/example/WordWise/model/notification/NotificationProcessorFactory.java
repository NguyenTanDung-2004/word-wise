package com.example.WordWise.model.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationProcessorFactory {
    @Autowired
    private MailNotificationProcessor mailNotificationProcessor;
    public NotificationProcessor createNotificationProcessor(Class<? extends NotificationProcessor> processorClass) {
        if (processorClass == MailNotificationProcessor.class) {
            return mailNotificationProcessor;
        }
        
        // Add more processors as needed
        return null;
    }
    
}
