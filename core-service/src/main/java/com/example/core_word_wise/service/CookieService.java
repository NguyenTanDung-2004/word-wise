package com.example.core_word_wise.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Tên cookie lưu JWT
    private static final String TOKEN_COOKIE_NAME = "token";

    /**
     * Thêm JWT token vào HTTP-only cookie
     * @param response HttpServletResponse để thêm cookie
     * @param token Chuỗi JWT
     */
    public void addTokenCookie(HttpServletResponse response, String token) {
        int maxAgeSeconds = (int) Duration.ofMillis(jwtExpirationMs).toSeconds();

        String cookie = String.format(
                "%s=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=None",
                TOKEN_COOKIE_NAME,
                token,
                maxAgeSeconds
        );

        response.addHeader("Set-Cookie", cookie);
    }


    /**
     * Xóa cookie token
     * @param response HttpServletResponse
     */
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}