package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserWordRepository extends JpaRepository<UserWord, Integer> {
    @Query("SELECT count(uw) FROM UserWord uw WHERE uw.collection.id = :collectionId")
    Long countByCollectionId(Integer collectionId);

    List<UserWord> findAllByCollection_CollectionId(Integer collectionId);
    boolean existsByUserAndWordAndCollection(User user, Word word, Collection collection);

    boolean existsByUserAndWord_WordId(User user, Integer wordId);

    Optional<UserWord> findByUserAndWord_WordIdAndCollection_CollectionId(User user, Integer wordId, Integer collectionId);
    boolean existsByWord_WordId(Integer wordId);

    List<UserWord> findByUserAndNextReviewDateLessThanEqual(User user, LocalDate date);
    List<UserWord> findByUserAndCollection_NameAndNextReviewDateLessThanEqual(User user, String collectionName, LocalDate date);

    List<UserWord> findByUserAndNextReviewDate(User user, LocalDate date);

    long countByUser(User user);
    long countByUserAndNextReviewDateLessThanEqual(User user, LocalDate date);

    Optional<UserWord> findByUserAndWord_WordId(User user, Integer wordId);

    List<UserWord> findByUserAndCollection_CollectionIdAndNextReviewDateLessThanEqual(User user, Integer collectionId, LocalDate date);

    @Query("SELECT uw FROM UserWord uw WHERE uw.user.id = :userId ORDER BY uw.familiarityScore ASC, uw.nextReviewDate ASC")
    List<UserWord> findWordsForExtensionPractice(Integer userId, Pageable pageable);

    List<UserWord> findByUserAndCollection_CollectionId(User user, Integer collectionId);

    // Đếm UserWord của user X trong collection Y
    Long countByUserAndCollection_CollectionId(User user, Integer collectionId);

    // Tìm tất cả UserWord của user X trong collection Y
    List<UserWord> findAllByUserAndCollection_CollectionId(User user, Integer collectionId);
}