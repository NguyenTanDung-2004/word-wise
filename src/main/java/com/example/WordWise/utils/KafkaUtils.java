package com.example.WordWise.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaUtils {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageToKafka(String topic, String textJson) {
        kafkaTemplate.send(topic, textJson);
    }

    public String replaceSpecificCharacters(String message) {
        return message.replaceAll("^\"|\"$", "").replace("\\", "");
    }
}
