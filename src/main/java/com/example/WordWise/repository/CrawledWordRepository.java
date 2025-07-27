package com.example.WordWise.repository;

import com.example.WordWise.entity.CrawledWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawledWordRepository extends JpaRepository<CrawledWord, String> {
}
