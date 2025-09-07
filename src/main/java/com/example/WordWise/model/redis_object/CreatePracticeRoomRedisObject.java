package com.example.WordWise.model.redis_object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePracticeRoomRedisObject {
    private String roomId;
    private Integer numberOfQuestions;
    private String userId;
    private String subjects;
}
