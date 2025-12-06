package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.entity.SavedCollection;
import com.example.core_word_wise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavedCollectionRepository extends JpaRepository<SavedCollection, Integer> {

    // Kiểm tra xem user đã save một collection cụ thể chưa
    boolean existsByUserAndOriginalCollection(User user, Collection collection);
    Optional<SavedCollection> findByUserAndOriginalCollection_Name(User user, String name);
    // Lấy tất cả các collection mà một user đã save
    List<SavedCollection> findByUser(User user);

    @Query("SELECT sc.originalCollection.name FROM SavedCollection sc WHERE sc.user = :user")
    List<String> findSavedCollectionNamesByUser(User user);

    Optional<SavedCollection> findByUserAndOriginalCollection(User user, Collection collection);
}