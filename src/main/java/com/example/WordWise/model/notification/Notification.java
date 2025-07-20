package com.example.WordWise.model.notification;

import lombok.AllArgsConstructor;
 import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Notification {
    protected String from;
    protected String to;
    protected String message;

    public abstract void sendNotification();
}
