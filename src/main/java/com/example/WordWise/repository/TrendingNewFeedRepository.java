package com.example.WordWise.repository;

import com.example.WordWise.entity.TrendingNewFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TrendingNewFeedRepository extends JpaRepository<TrendingNewFeed, String> {
    @Query(nativeQuery = true, value = """
        SELECT * FROM trending_new_feed tnf
        WHERE DATE(tnf.created_date) = :localDate
    """)
    List<TrendingNewFeed> getNewFeed(LocalDate localDate);
}
