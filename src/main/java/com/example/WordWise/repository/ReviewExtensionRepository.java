package com.example.WordWise.repository;

import com.example.WordWise.entity.ExtensionReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewExtensionRepository extends JpaRepository<ExtensionReview, String> {
}
