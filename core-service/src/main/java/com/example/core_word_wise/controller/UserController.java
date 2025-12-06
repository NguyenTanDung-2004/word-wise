package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.chat.SendMessageRequest;
import com.example.core_word_wise.dto.friend.FriendRequestResponse;
import com.example.core_word_wise.dto.friend.FriendResponse;
import com.example.core_word_wise.dto.user.*;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.security.JwtService;
import com.example.core_word_wise.service.ChatService;
import com.example.core_word_wise.service.CookieService;
import com.example.core_word_wise.service.FriendshipService;
import com.example.core_word_wise.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final FriendshipService friendshipService;
    private final ChatService chatService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getLoggedInUserInfo(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        User freshUser = userService.getUserById(currentUser.getUserId());

        UserResponse userResponse = UserResponse.builder()
                .userId(freshUser.getUserId())
                .username(freshUser.getDisplayName())
                .email(freshUser.getEmail())
                .avatarUrl(freshUser.getAvatarUrl())
                .role(freshUser.getRole().name())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userResponse, "User information retrieved successfully."));
    }

    @PutMapping("/update-info")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUserInfo(
            Authentication authentication,
            @Valid @RequestBody UpdateUserInfoRequest request,
            HttpServletResponse response
    ) {
        User principalUser = (User) authentication.getPrincipal();
        Integer userId = principalUser.getUserId();

        User updatedUser = userService.updateUserInfoById(userId, request);

        String newToken = jwtService.generateToken(updatedUser);

        cookieService.addTokenCookie(response, newToken);

        UserResponse userResponse = UserResponse.builder()
                .userId(updatedUser.getUserId())
                .username(updatedUser.getDisplayName())
                .email(updatedUser.getEmail())
                .avatarUrl(updatedUser.getAvatarUrl())
                .role(updatedUser.getRole().name())
                .build();

        UpdateUserResponse finalResponse = UpdateUserResponse.builder()
                .userInfo(userResponse)
                .newToken(newToken)
                .build();

        return ResponseEntity.ok(ApiResponse.success(finalResponse, "User information updated successfully. A new token has been issued."));
    }

    // tìm kiếm ngời dùng bằng tên
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUsers(
            @RequestParam String username,
            @AuthenticationPrincipal User currentUser
    ) {
        List<UserSearchResponse> users = friendshipService.searchUsers(username, currentUser);
        return ResponseEntity.ok(ApiResponse.success(users, "Users found successfully."));
    }


    // gửi lời mời kết bạn
    @PostMapping("/friends/request/{receiverId}")
    public ResponseEntity<ApiResponse<Void>> sendFriendRequest(
            @AuthenticationPrincipal User sender,
            @PathVariable Integer receiverId
    ) {
        friendshipService.sendFriendRequest(sender, receiverId);
        return ResponseEntity.ok(ApiResponse.success(null, "Friend request sent successfully."));
    }

    // chấp nhận lời mời
    @PostMapping("/friends/accept/{requesterId}")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer requesterId
    ) {
        friendshipService.acceptFriendRequest(currentUser, requesterId);
        return ResponseEntity.ok(ApiResponse.success(null, "Friend request accepted."));
    }

    // từ chối lời mời
    @DeleteMapping("/friends/reject/{requesterId}")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer requesterId
    ) {
        friendshipService.rejectFriendRequest(currentUser, requesterId);
        return ResponseEntity.ok(ApiResponse.success(null, "Friend request rejected."));
    }

    // lấy danh sách bạn bè
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<FriendResponse>>> getFriendsList(@AuthenticationPrincipal User user) {
        List<FriendResponse> friends = friendshipService.getFriends(user);
        return ResponseEntity.ok(ApiResponse.success(friends));
    }

    // lấy danh sách lời mời kết bạn đã nhận
    @GetMapping("/friends/requests")
    public ResponseEntity<ApiResponse<List<FriendRequestResponse>>> getFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequestResponse> requests = friendshipService.getPendingFriendRequests(user);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    // Unfriend
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<ApiResponse<Void>> unfriend(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer friendId
    ) {
        friendshipService.unfriend(currentUser, friendId);
        return ResponseEntity.ok(ApiResponse.success(null, "Unfriended successfully."));
    }

}