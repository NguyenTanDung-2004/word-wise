package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.practice.CompletePracticeRequest;
import com.example.core_word_wise.dto.practice.CompletePracticeResponse;
import com.example.core_word_wise.dto.practice.PracticeSessionDTO;
import com.example.core_word_wise.dto.practice.PracticeWordDTO;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.PracticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;


    // Lấy danh sách từ cần học hôm nay (chung hoặc theo collection)
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<PracticeSessionDTO>> getPracticeSession(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String collectionName
    ) {
        PracticeSessionDTO session = practiceService.getPracticeSessionForToday(user, collectionName);
        return ResponseEntity.ok(ApiResponse.success(session, "Practice session created successfully."));
    }

    // Hoàn tất một phiên học đã được tạo trước (cả chung và collection)
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<CompletePracticeResponse>> completePractice(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CompletePracticeRequest request
    ) {
        CompletePracticeResponse response = practiceService.completePracticeSession(user, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Hoàn tất một phiên học tùy chỉnh (người dùng tự chọn từ)
    @PostMapping("/complete-custom")
    public ResponseEntity<ApiResponse<CompletePracticeResponse>> completeCustomPractice(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody List<CompletePracticeRequest.WordResult> results
    ) {
        CompletePracticeResponse response = practiceService.completeCustomPracticeSession(user, results);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Lấy danh sách ôn từ của extension
    @GetMapping("/extension-words")
    public ResponseEntity<ApiResponse<List<PracticeWordDTO>>> getWordsForExtension(@AuthenticationPrincipal User user) {
        List<PracticeWordDTO> words = practiceService.getWordsForExtension(user);
        return ResponseEntity.ok(ApiResponse.success(words));
    }
}