package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.setting.UpdateUserSettingRequest;
import com.example.core_word_wise.dto.setting.UserSettingDTO;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.entity.UserSetting;
import com.example.core_word_wise.repository.UserRepository;
import com.example.core_word_wise.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserSettingDTO getUserSetting(User user) {
        UserSetting setting = findOrCreateSetting(user);
        return mapToDTO(setting);
    }

    @Transactional
    public UserSettingDTO upsertUserSetting(User user, UpdateUserSettingRequest request) {
        // Gọi hàm helper với userId thay vì cả đối tượng User
        UserSetting setting = findOrCreateSettingForUpdate(user.getUserId());

        setting.setStudySessionsPerDay(request.getStudySessionsPerDay());
        setting.setWordsPerSession(request.getWordsPerSession());
        setting.setLastUpdated(LocalDateTime.now());

        UserSetting savedSetting = userSettingRepository.save(setting);
        return mapToDTO(savedSetting);
    }

    // Hàm cho các thao tác CHỈ ĐỌC
    private UserSetting findOrCreateSetting(User user) {
        return userSettingRepository.findById(user.getUserId())
                .orElseGet(() -> {
                    UserSetting defaultSetting = new UserSetting();
                    defaultSetting.setUserId(user.getUserId());
                    defaultSetting.setStudySessionsPerDay(3);
                    defaultSetting.setWordsPerSession(15);
                    return defaultSetting;
                });
    }

    // Hàm cho các thao tác GHI (UPDATE/CREATE)
    private UserSetting findOrCreateSettingForUpdate(Integer userId) {
        Optional<UserSetting> settingOpt = userSettingRepository.findByIdForUpdate(userId);

        if (settingOpt.isPresent()) {
            return settingOpt.get();
        } else {
            try {
                User managedUser = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("User not found during setting creation."));

                UserSetting newSetting = new UserSetting();
                newSetting.setUserId(managedUser.getUserId());
                newSetting.setUser(managedUser);
                newSetting.setStudySessionsPerDay(3);
                newSetting.setWordsPerSession(15);
                return userSettingRepository.saveAndFlush(newSetting);
            } catch (DataIntegrityViolationException e) {
                return userSettingRepository.findByIdForUpdate(userId)
                        .orElseThrow(() -> new IllegalStateException("Failed to fetch user setting after race condition."));
            }
        }
    }

    // Hàm helper để ánh xạ Entity sang DTO
    private UserSettingDTO mapToDTO(UserSetting setting) {
        return UserSettingDTO.builder()
                .userId(setting.getUserId())
                .settings(UserSettingDTO.Settings.builder()
                        .studySessionsPerDay(setting.getStudySessionsPerDay())
                        .wordsPerSession(setting.getWordsPerSession())
                        .build())
                .lastUpdated(setting.getLastUpdated())
                .build();
    }
}