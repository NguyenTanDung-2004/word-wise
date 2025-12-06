package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.post.PostResponse;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // News Feed
    @GetMapping("/news-feed")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getNewsFeed(@AuthenticationPrincipal User user) {
        List<PostResponse> newsFeed = communityService.getNewsFeed(user);
        return ResponseEntity.ok(ApiResponse.success(newsFeed, "News feed retrieved successfully."));
    }

    // Lấy danh sách thông báo shared collection
    @GetMapping("/shared-notifications")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getSharedCollectionNotifications(@AuthenticationPrincipal User user) {
        List<PostResponse> notifications = communityService.getSharedCollectionNotifications(user);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Shared collection notifications retrieved successfully."));
    }

    // Lưu một collections shared
    @PostMapping("/save-collection/{postId}")
    public ResponseEntity<ApiResponse<Void>> saveSharedCollection(
            @AuthenticationPrincipal User user,
            @PathVariable Integer postId
    ) {
        communityService.saveSharedCollection(user, postId);
        return ResponseEntity.ok(ApiResponse.success(null, "Collection saved successfully."));
    }
}