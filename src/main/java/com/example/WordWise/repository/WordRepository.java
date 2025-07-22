package com.example.WordWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WordWise.entity.Word;

public interface WordRepository extends JpaRepository<Word, String> {
    
}
