package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Integer> {

    Optional<GameMode> findByName(String name);
}