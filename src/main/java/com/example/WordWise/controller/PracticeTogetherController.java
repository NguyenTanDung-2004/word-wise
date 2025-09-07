package com.example.WordWise.controller;

import com.example.WordWise.dto.request.RequestCreatePracticeTogetherRoom;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.PracticeTogetherRoom;
import com.example.WordWise.entity.User;
import com.example.WordWise.enums.KafkaTopics;
import com.example.WordWise.repository.PracticeTogetherRoomRepository;
import com.example.WordWise.service.PracticeTogetherService;
import com.example.WordWise.service.UserService;
import com.example.WordWise.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity createRoom(Authentication authentication, @RequestBody RequestCreatePracticeTogetherRoom request) {
        User user = Utils.getUserIdFromSecurityConfig(authentication, userService);
        String handShakeToken = practiceTogetherService.createRoom(user, request);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(handShakeToken)
                .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //@KafkaListener(groupId = "word-wise", topics = "CREATE_PRACTICE_ROOM")
    public void generateQuestion(String message) {
        practiceTogetherService.generateQuestions(message);
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Inject SimpMessagingTemplate
    @GetMapping("/test")
    public String test() {
        Map<String, Object> datas = new HashMap<>();
        datas.put("message", "Hello, this is a test message!");
        datas.put("user", "Test User");
        messagingTemplate.convertAndSend("/topic/admin/ac99377b-e74f-4fa6-855a-778eb6b223ba", datas);
        return "ok";
    }
}
