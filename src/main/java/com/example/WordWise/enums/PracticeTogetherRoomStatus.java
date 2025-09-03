package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PracticeTogetherRoomStatus {
    PROCESSING(1, "P"),
    READY(2, "R"),
    DELETE(3, "D");
    private int id;
    private String key;

    public static PracticeTogetherRoomStatus fromId(String key) {
        for (PracticeTogetherRoomStatus practiceTogetherRoomStatus : PracticeTogetherRoomStatus.values()) {
            if (practiceTogetherRoomStatus.getKey().equals(key)) {
                return practiceTogetherRoomStatus;
            }
        }
        throw new IllegalArgumentException("No KafkaTopics found for key: " + key);
    }
}
