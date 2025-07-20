package com.example.WordWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.WordWise.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    public User findByEmail(String email);
}
