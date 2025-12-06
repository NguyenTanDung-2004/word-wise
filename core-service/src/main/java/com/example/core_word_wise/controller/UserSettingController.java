package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.setting.UpdateUserSettingRequest;
import com.example.core_word_wise.dto.setting.UserSettingDTO;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.UserSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    // Lấy thông tin cài đặt
    @GetMapping
    public ResponseEntity<ApiResponse<UserSettingDTO>> getUserSettings(@AuthenticationPrincipal User user) {
        UserSettingDTO settings = userSettingService.getUserSetting(user);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    // Tạo hoặc cập nhật cài đặt
    @PostMapping
    public ResponseEntity<ApiResponse<UserSettingDTO>> upsertUserSettings(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserSettingRequest request
    ) {
        UserSettingDTO updatedSettings = userSettingService.upsertUserSetting(user, request);
        return ResponseEntity.ok(ApiResponse.success(updatedSettings, "Settings updated successfully."));
    }
}