package com.example.WordWise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.WordWise.entity.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, String> {

    @Query(value = "SELECT * FROM collections WHERE user_id = :userId ORDER BY updated_date DESC LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Collection> getListCollections(@Param("userId") String userId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM collections WHERE user_id = :userId", nativeQuery = true)
    Long countCollectionsByUserId(@Param("userId") String userId);

    Optional<Collection> findByIdAndUserId(String id, String userId);

    List<Collection> findByUserId(String userId);
}

