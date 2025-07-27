package com.example.WordWise.repository;

import com.example.WordWise.entity.TrendingNewFeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendingNewFeedRepository extends JpaRepository<TrendingNewFeed, String> {
}
