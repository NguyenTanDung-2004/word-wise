package com.example.core_word_wise.dto.challenge;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class InviteFriendsRequest {
    @NotEmpty
    private List<Integer> friendIds;
}