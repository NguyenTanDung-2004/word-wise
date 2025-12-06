package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.user.ChangePasswordRequest;
import com.example.core_word_wise.dto.user.UpdateUserInfoRequest;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        // 1. Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getReNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation password do not match.");
        }

        // 2. Kiểm tra mật khẩu hiện tại có đúng không
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        // 3. Cập nhật mật khẩu mới (đã mã hóa)
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public User updateUserInfoById(Integer userId, UpdateUserInfoRequest request) {
        User userToUpdate = getUserById(userId);

        boolean updated = false;

        String newUsername = request.getUsername();

        if (newUsername != null && !newUsername.isBlank()) {
            if (!userToUpdate.getUsername().equals(newUsername)) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new IllegalArgumentException("Username '" + newUsername + "' is already taken.");
                }
                userToUpdate.setUsername(newUsername);
                updated = true;
            }
        }

        String newAvatar = request.getAvatar();

        if (newAvatar != null) {
            userToUpdate.setAvatarUrl(newAvatar);
            updated = true;
        }

        if (updated) {
            userToUpdate.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(userToUpdate);
        }

        return userToUpdate;
    }
}