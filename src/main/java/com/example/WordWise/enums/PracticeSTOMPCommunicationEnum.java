package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PracticeSTOMPCommunicationEnum {
    CREATE_ROOM(1, "CREATE_ROOM"),
    REQUEST_JOIN(2, "REQUEST_JOIN"),
    PERSONAL_ROOM(3, "PERSONAL_ROOM");
    private int id;
    private String key;

    public static PracticeSTOMPCommunicationEnum fromKey(String key) {
        for (PracticeSTOMPCommunicationEnum practiceSTOMPCommunicationEnum : PracticeSTOMPCommunicationEnum.values()) {
            if (practiceSTOMPCommunicationEnum.getKey().equals(key)) {
                return practiceSTOMPCommunicationEnum;
            }
        }
        throw new IllegalArgumentException("No KafkaTopics found for key: " + key);
    }

    public static String getFull() {
        StringBuilder strB = new StringBuilder();
        for (PracticeRoomQuestionType type : PracticeRoomQuestionType.values()) {
            strB.append(type.getKey());
            strB.append(" ");
        }

        return strB.toString();
    }
}
