package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PracticeRoomQuestionType {

    TRANSLATE(1, "TRANSLATE"),
    SELECT(2, "SELECT"),
    COMPLETE(3, "COMPLETE");
    private int id;
    private String key;

    public static PracticeRoomQuestionType fromId(String key) {
        for (PracticeRoomQuestionType practiceRoomQuestionType : PracticeRoomQuestionType.values()) {
            if (practiceRoomQuestionType.getKey().equals(key)) {
                return practiceRoomQuestionType;
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
