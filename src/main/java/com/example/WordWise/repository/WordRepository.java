package com.example.WordWise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.WordWise.entity.Word;

public interface WordRepository extends JpaRepository<Word, String> {

    @Query(
        value = "SELECT * FROM words WHERE user_id = :userId ORDER BY created_date DESC LIMIT :pageSize OFFSET :offset",
        nativeQuery = true
    )
    List<Word> getListWords(
        @Param("userId") String userId,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );
    
}
