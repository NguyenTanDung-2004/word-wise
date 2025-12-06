package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.pronunciation.SavePronunciationRequest;
import com.example.core_word_wise.dto.pronunciation.SavePronunciationResponse;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.PronunciationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pronunciation")
@RequiredArgsConstructor
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @PostMapping("/save-result")
    public ResponseEntity<ApiResponse<SavePronunciationResponse>> saveResult(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SavePronunciationRequest request
    ) {
        SavePronunciationResponse response = pronunciationService.savePronunciationResult(user, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}