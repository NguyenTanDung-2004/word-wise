package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Integer> {
    Optional<Word> findByWordText(String wordText);

    @Query(value = "SELECT * FROM word ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Word> findRandomWords(int count);

}