package com.example.core_word_wise.controller;

import com.example.core_word_wise.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GamePlayController {

    private final GameService gameService;

    @MessageMapping("/challenge-room/{roomId}/start")
    public void startGame(@DestinationVariable Integer roomId, Principal principal) { // <-- Sửa thành Principal
        if (principal != null) {
            // Lấy userId từ name của principal và chuyển thành Integer
            Integer hostId = Integer.parseInt(principal.getName());
            gameService.startGame(roomId, hostId);
        }
    }

    @MessageMapping("/challenge-room/{roomId}/score")
    public void submitScore(
            @DestinationVariable Integer roomId,
            @Payload Map<String, Integer> payload,
            Principal principal // <-- Sửa thành Principal
    ) {
        if (principal != null && payload.containsKey("score")) {
            // Lấy userId từ name của principal
            Integer userId = Integer.parseInt(principal.getName());
            // Chúng ta cần tìm lại đối tượng User từ userId
            // Để đơn giản, chúng ta sẽ sửa lại service để nhận userId
            gameService.updateUserScore(roomId, userId, payload.get("score"));
        }
    }
}