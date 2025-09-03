package com.example.WordWise.controller;

import com.example.WordWise.dto.request.RequestCreatePracticeTogetherRoom;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.entity.User;
import com.example.WordWise.repository.PracticeTogetherRoomRepository;
import com.example.WordWise.service.PracticeTogetherService;
import com.example.WordWise.service.UserService;
import com.example.WordWise.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/practice-together")
public class PracticeTogetherController {
    @Autowired
    private UserService userService;

    @Autowired
    private PracticeTogetherService practiceTogetherService;

    @PostMapping("/{roomId}/request-join")
    public ResponseEntity requestJoin(Authentication authentication, @PathVariable(name = "roomId") String roomId) {
        String userId = Utils.getUserIdFromSecurityConfig(authentication);
        practiceTogetherService.requestJoin(roomId, userId);

        ApiResponse response = ApiResponse.builder()
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();

        return ResponseEntity.ok(response);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PracticeTogetherRoomRepository roomRepository;

    @PostMapping("")
    @Transactional
    public ResponseEntity createRoom(Authentication authentication, @RequestBody RequestCreatePracticeTogetherRoom request) {
        User user = Utils.getUserIdFromSecurityConfig(authentication, userService);
        List<PracticeTogetherRoom> list = roomRepository.findByUser(user);
        Hibernate.initialize(user.getPracticeTogetherRooms());
        PracticeTogetherRoom practiceTogetherRoom = practiceTogetherService.createRoom(user, request);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(practiceTogetherRoom)
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
