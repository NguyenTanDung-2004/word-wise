package com.example.core_word_wise.dto.challenge;

import com.example.core_word_wise.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RoomResponse {
    private Integer roomId;
    private String inviteCode;
    private String gameMode;
    private String status;
    private UserResponse host;
    private List<UserResponse> participants;
}