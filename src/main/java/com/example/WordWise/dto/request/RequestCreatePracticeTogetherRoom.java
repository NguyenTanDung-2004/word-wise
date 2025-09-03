package com.example.WordWise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestCreatePracticeTogetherRoom {
    private String name;
    private Integer numberOfMembers;
    private Integer numberOfQuestions;
    private Boolean isPublic;
    private String password;
    private String roomCode;
}
