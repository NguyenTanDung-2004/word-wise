package com.example.WordWise.controller;

import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.dto.response.TrendingNewFeedResponse;
import com.example.WordWise.service.TrendingNewFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/new-feed")
public class NewFeedController {
    @Autowired
    private TrendingNewFeedService trendingNewFeedService;

    @GetMapping("")
    public ResponseEntity getNewFeeds() {
        List<TrendingNewFeedResponse> newFeedList = this.trendingNewFeedService.getTrendingNewFeedResponseList();
        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.CREATE_USER_SUCCESS))
                .object(newFeedList)
                .build();

        return ResponseEntity.ok(response);
    }
}
