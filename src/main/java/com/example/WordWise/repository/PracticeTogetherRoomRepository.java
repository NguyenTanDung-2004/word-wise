package com.example.WordWise.repository;

import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeTogetherRoomRepository extends JpaRepository<PracticeTogetherRoom, String> {
    List<PracticeTogetherRoom> findByUser(User user);
}
