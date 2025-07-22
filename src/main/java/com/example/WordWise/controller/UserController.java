package com.example.WordWise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.WordWise.dto.request.CreateUserRequest;
import com.example.WordWise.dto.request.LoginRequest;
import com.example.WordWise.dto.request.ResetPasswordRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.dto.response.UserResponse;
import com.example.WordWise.entity.User;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private Mapper mapper;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);
        UserResponse userResponse = new UserResponse();
        mapper.map(user, userResponse);

        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.CREATE_USER_SUCCESS))
                .object(userResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody LoginRequest loginRequest) {
        String accessToken = this.userService.genAccessTokenWhenLogin(loginRequest);
        // Logic for user login would go here
        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.LOGIN_SUCCESS))
                .object(accessToken)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        if (resetPasswordRequest.getCode() == null || resetPasswordRequest.getCode().isEmpty()) {
            userService.sendCodeViaEmail(resetPasswordRequest);
            ApiResponse response = ApiResponse.builder()
                    .enumResponse(EnumResponse.toJson(EnumResponse.SEND_CODE_FORGOT_PASSWORD_SUCCESS))
                    .object(null)
                    .build();
            return ResponseEntity.ok(response);
        }

        userService.updateUserPassword(resetPasswordRequest);
        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.SEND_CODE_FORGOT_PASSWORD_SUCCESS))
                .object(null)
                .build();
        return ResponseEntity.ok(response);
    }
    
}
