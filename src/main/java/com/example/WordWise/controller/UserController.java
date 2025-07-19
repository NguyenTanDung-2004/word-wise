package com.example.WordWise.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.WordWise.dto.request.CreateUserRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody CreateUserRequest createUserRequest) {
        // Logic to handle user registration
        // For now, just return the request data as a response
        return ResponseEntity.ok(createUserRequest);
    }
    
}
