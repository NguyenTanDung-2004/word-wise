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

    @Query(value = """
           SELECT w.*
           FROM words w
           LEFT JOIN (
               SELECT er.word_id
               FROM extension_review er
               WHERE er.user_id = :userId
                 AND er.is_true = true
                 AND er.type = :typeId
               ORDER BY er.test_date DESC
               LIMIT 50
           ) AS recent_reviews
           ON w.id = recent_reviews.word_id
           WHERE w.user_id = :userId
             AND recent_reviews.word_id IS NULL
           LIMIT 1;
            """, nativeQuery = true)
    public Word getReviewExtensionWord(String userId, int typeId);
    
    @Query(value = "SELECT * FROM words WHERE collection_id = :collectionId ORDER BY created_date DESC LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Word> getWordsByCollectionId(@Param("collectionId") String collectionId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM words WHERE collection_id = :collectionId", nativeQuery = true)
    Long countWordsByCollectionId(@Param("collectionId") String collectionId);

    List<Word> findByCollectionId(String collectionId);

}
