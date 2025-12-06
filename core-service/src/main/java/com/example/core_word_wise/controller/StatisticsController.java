package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.stats.*;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.StatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyStatsDTO>> getDailyStats(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyStatsDTO stats = statisticsService.getDailyStatistics(user, date);
        return ResponseEntity.ok(ApiResponse.success(stats, "Daily statistics retrieved successfully."));
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeStatsDTO>> getHomeStats(@AuthenticationPrincipal User user) {
        HomeStatsDTO stats = statisticsService.getHomeStatistics(user);
        return ResponseEntity.ok(ApiResponse.success(stats, "Home statistics retrieved successfully."));
    }

    @GetMapping("/mobile/general")
    public ResponseEntity<ApiResponse<GeneralStatsResponse>> getGeneralStats(
            @AuthenticationPrincipal User user,
            @Valid GeneralStatsRequest request
    ) {
        GeneralStatsResponse stats = statisticsService.getGeneralStatistics(user, request);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    @GetMapping("/mobile/home")
    public ResponseEntity<ApiResponse<CoreHomeStatsDTO>> getCoreHomeStats(@AuthenticationPrincipal User user) {
        CoreHomeStatsDTO stats = statisticsService.getCoreHomeStatistics(user);
        return ResponseEntity.ok(ApiResponse.success(stats, "Core home statistics retrieved successfully."));
    }

    @GetMapping("/mobile/collection-progress")
    public ResponseEntity<ApiResponse<CollectionProgressDTO>> getCollectionProgress(
            @AuthenticationPrincipal User user,
            @RequestParam String collectionName
    ) {
        CollectionProgressDTO progress = statisticsService.getCollectionProgress(user, collectionName);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
}