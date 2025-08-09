package com.example.WordWise.controller;

import com.example.WordWise.entity.User;
import com.example.WordWise.service.UserService;
import com.example.WordWise.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review-extension")
public class ExtensionReviewController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity getReviewQuestion(Authentication authentication) {
        String userId = Utils.getUserIdFromSecurityConfig(authentication);
        User user = this.userService.getUserFromId(userId);

        return null;
    }
}
