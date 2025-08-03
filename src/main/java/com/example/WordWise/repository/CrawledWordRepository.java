package com.example.WordWise.repository;

import com.example.WordWise.entity.CrawledWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrawledWordRepository extends JpaRepository<CrawledWord, String> {

    @Query(nativeQuery = true, value = """
        SELECT * FROM crawled_words cw
        WHERE cw.trending_new_feed_id IN(:ids)
    """)
    List<CrawledWord> getWordInNewFeed(@Param("ids") List<String> ids);
}
