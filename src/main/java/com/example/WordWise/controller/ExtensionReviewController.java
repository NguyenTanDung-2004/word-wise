package com.example.WordWise.controller;

import com.example.WordWise.dto.request.SubmitExtensionReviewRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.ExtensionReview;
import com.example.WordWise.entity.User;
import com.example.WordWise.service.ExtensionReviewService;
import com.example.WordWise.service.UserService;
import com.example.WordWise.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review-extension")
public class ExtensionReviewController {
    @Autowired
    private UserService userService;

    @Autowired
    private ExtensionReviewService extensionReviewService;

    @GetMapping("")
    public ResponseEntity getReviewQuestion(Authentication authentication) {
        String userId = Utils.getUserIdFromSecurityConfig(authentication);
        User user = this.userService.getUserFromId(userId);

        ExtensionReview extensionReview = extensionReviewService.getExtensionReview(userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(extensionReview)
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("")
    public ResponseEntity submit(Authentication authentication, @RequestBody SubmitExtensionReviewRequest submitExtensionReviewRequest) {
        String userId = Utils.getUserIdFromSecurityConfig(authentication);
        User user = this.userService.getUserFromId(userId);

        extensionReviewService.submit(submitExtensionReviewRequest);

        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();

        return ResponseEntity.ok(response);
    }
}
