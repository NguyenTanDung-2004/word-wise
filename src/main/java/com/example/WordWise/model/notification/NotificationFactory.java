package com.example.WordWise.model.notification;

public class NotificationFactory {
    
    public static Notification createMailNotification(Class<? extends Notification> notificationClass, String from, String to, String message) {
        if (notificationClass == MailNotification.class) {
            return new MailNotification();
        }
       
        return null;
    }
}
