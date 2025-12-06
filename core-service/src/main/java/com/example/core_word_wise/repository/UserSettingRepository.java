package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.UserSetting;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT us FROM UserSetting us WHERE us.userId = :userId")
    Optional<UserSetting> findByIdForUpdate(Integer userId);
}