package com.example.WordWise.repository;

import com.example.WordWise.entity.LoggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LoggerEntity, Long> {}
