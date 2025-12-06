package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.auth.*;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.UserRepository;
import com.example.core_word_wise.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final EmailService emailService;
    private final Map<String, PasswordResetToken> passwordResetCache = new HashMap<>();

    private static class PasswordResetToken {
        String code;
        LocalDateTime expiryTime;

        public PasswordResetToken(String code, Duration duration) {
            this.code = code;
            this.expiryTime = LocalDateTime.now().plus(duration);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }
    private static final Duration OTP_EXPIRY_DURATION = Duration.ofMinutes(5);

    @Transactional
    public AuthResponse register(RegisterRequest request)  {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không khớp.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        // Tạo username dựa trên full name (đúng theo API yêu cầu)
        String baseUsername = request.getFullName().replaceAll("\\s+", "");
        String username = generateUniqueUsername(baseUsername);

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setAvatarUrl("https://img.freepik.com/premium-vector/person-with-blue-shirt-that-says-name-person_1029948-7040.jpg?semt=ais_hybrid&w=740&q=80");
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // Role mặc định là USER

        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }


    private String generateUniqueUsername(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        String randomSuffix = UUID.randomUUID().toString().substring(0, 5);
        String username = base + "_" + randomSuffix;

        while (userRepository.existsByUsername(username)) {
            randomSuffix = UUID.randomUUID().toString().substring(0, 5);
            username = base + "_" + randomSuffix;
        }
        return username;
    }


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist."));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Password is incorrect.");
        } catch (Exception e) {
            throw new RuntimeException("Unknown authentication error.", e);
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatar(user.getAvatarUrl())
                .token(jwtToken)
                .build();
    }



    // Bước 1: Yêu cầu Forgot Password (Gửi OTP)
    public String requestForgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại trong hệ thống."));

        // 1. Tạo mã OTP ngẫu nhiên (4 số theo yêu cầu API)
        String otp = String.format("%04d", (int) (Math.random() * 9999));

        // 2. Lưu cache: email -> OTP + Thời gian hết hạn (5 phút)
        passwordResetCache.put(request.getEmail(), new PasswordResetToken(otp, OTP_EXPIRY_DURATION));

        // 3. Gửi email
        String subject = "[WordWise] Mã xác minh đặt lại mật khẩu của bạn";
        String body = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f9fafb; margin: 0; padding: 0;">
            <div style="max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 30px;">
                <h2 style="color: #333333; text-align: center;">Xin chào %s </h2>
                <p style="font-size: 15px; color: #555555;">
                    Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản <b>WordWise</b>.
                </p>
                <p style="font-size: 15px; color: #555555; margin-bottom: 25px;">
                    Mã xác minh của bạn là:
                </p>
                <div style="text-align: center; margin: 20px 0;">
                    <span style="display: inline-block; background-color: #007bff; color: white; font-size: 22px; font-weight: bold; padding: 12px 24px; border-radius: 8px; letter-spacing: 2px;">
                        %s
                    </span>
                </div>
                <p style="font-size: 14px; color: #777777;">
                    Mã này sẽ hết hạn sau <b>5 phút</b>. Vui lòng <b>không chia sẻ mã này</b> với bất kỳ ai vì lý do bảo mật.
                </p>
                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 25px 0;">
                <p style="font-size: 14px; color: #888888; text-align: center;">
                    Trân trọng,<br>
                    <b>Đội ngũ WordWise</b>
                </p>
            </div>
        </body>
        </html>
        """, user.getFullName(), otp);


        emailService.sendHtmlEmail(request.getEmail(), subject, body);

        return request.getEmail();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetCache.get(request.getEmail());

        if (token == null) {
            throw new IllegalArgumentException("Yêu cầu đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
        }

        if (token.isExpired()) {
            passwordResetCache.remove(request.getEmail());
            throw new IllegalArgumentException("Mã xác minh đã hết hạn. Vui lòng yêu cầu lại.");
        }

        if (!token.code.equals(request.getCode())) {
            throw new IllegalArgumentException("Mã xác minh không chính xác.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP khỏi cache sau khi thành công
        passwordResetCache.remove(request.getEmail());
    }
}