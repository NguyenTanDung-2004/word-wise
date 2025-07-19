package com.example.WordWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WordWise.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    // Define methods for user-related database operations if needed
    
}
