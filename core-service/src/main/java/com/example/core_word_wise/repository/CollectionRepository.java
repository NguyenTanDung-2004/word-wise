package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    List<Collection> findAllByUser(User user);
    Optional<Collection> findByNameAndUser(String name, User user);

    @Query("SELECT c.name FROM Collection c WHERE c.user = :user AND c.isDeleted = false")
    List<String> findCollectionNamesByUserAndIsDeletedFalse(User user);

    List<Collection> findAllByUserAndIsDeletedFalse(User user);
    Optional<Collection> findByNameAndUserAndIsDeletedFalse(String name, User user);
    Optional<Collection> findByCollectionIdAndIsDeletedFalse(Integer collectionId);

    boolean existsByName(String name);

    long countByUserAndIsDeletedFalse(User user);

    boolean existsByNameAndUserAndIsDeletedFalse(String name, User user);
}