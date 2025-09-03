package com.example.WordWise.model.KafkaObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePracticeRoomKafkaObject {
    private String roomId;
    private Integer numberOfQuestions;
    private String userId;
}
