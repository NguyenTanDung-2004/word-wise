package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.auth.*;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.security.JwtService;
import com.example.core_word_wise.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.core_word_wise.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse responseData = authService.register(request);
        return new ResponseEntity<>(ApiResponse.success(responseData, "Registration successful."), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse responseData = authService.login(request);
        cookieService.addTokenCookie(response, responseData.getToken());
        return ResponseEntity.ok(ApiResponse.success(responseData, "Login successful."));
    }

    @PostMapping("/forgot-password/request")
    public ResponseEntity<ApiResponse<String>> requestForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String email = authService.requestForgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(email, "Verification code sent successfully."));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password has been reset successfully."));
    }

    @GetMapping("/extension-token")
    public ResponseEntity<ApiResponse<ExtensionTokenResponse>> getExtensionToken(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new SecurityException("User must be authenticated to request an extension token.");
        }

        String longLivedToken = jwtService.generateExtensionToken(user);

        ExtensionTokenResponse responseDto = ExtensionTokenResponse.builder()
                .token(longLivedToken)
                .build();

        return ResponseEntity.ok(ApiResponse.success(responseDto, "Long-lived token issued for extension use."));
    }
}