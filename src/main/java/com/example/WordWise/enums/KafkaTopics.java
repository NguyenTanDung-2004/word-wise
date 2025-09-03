package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum KafkaTopics {
    CREATE_PRACTICE_ROOM(1, "CREATE_PRACTICE_ROOM");
    private int id;
    private String key;

    public static KafkaTopics fromId(String key) {
        for (KafkaTopics kafkaTopic : KafkaTopics.values()) {
            if (kafkaTopic.getKey().equals(key)) {
                return kafkaTopic;
            }
        }
        throw new IllegalArgumentException("No KafkaTopics found for key: " + key);
    }
}