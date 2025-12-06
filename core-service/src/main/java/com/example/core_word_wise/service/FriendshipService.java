package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.friend.FriendRequestResponse;
import com.example.core_word_wise.dto.friend.FriendResponse;
import com.example.core_word_wise.dto.user.UserSearchResponse;
import com.example.core_word_wise.entity.Friendship;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.FriendshipRepository;
import com.example.core_word_wise.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // Tìm kiếm người dùng theo username
    public List<UserSearchResponse> searchUsers(String username, User currentUser) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream()
                .filter(user -> !user.getUserId().equals(currentUser.getUserId()))
                .map(user -> {
                    String status = getFriendshipStatus(currentUser, user);
                    return UserSearchResponse.builder()
                            .userId(user.getUserId())
                            .username(user.getDisplayName())
                            .avatarUrl(user.getAvatarUrl())
                            .friendshipStatus(status)
                            .build();
                }).collect(Collectors.toList());
    }

    // Gửi lời mời kết bạn
    @Transactional
    public Friendship sendFriendRequest(User sender, Integer receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found."));

        if (sender.getUserId().equals(receiverId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findFriendshipBetween(sender, receiver);
        if (existingFriendship.isPresent()) {
            throw new IllegalArgumentException("A friend request already exists or you are already friends.");
        }

        Friendship friendship = new Friendship();
        friendship.setUserOne(sender);
        friendship.setUserTwo(receiver);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        friendship.setActionUser(sender);
        friendship.setCreatedAt(LocalDateTime.now());

        return friendshipRepository.save(friendship);
    }

    // Chấp nhận lời mời kết bạn
    @Transactional
    public Friendship acceptFriendRequest(User currentUser, Integer requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found."));

        // Sử dụng phương thức truy vấn mới, rõ ràng và chính xác
        Friendship friendship = friendshipRepository.findByUserOneAndUserTwoAndStatus(requester, currentUser, Friendship.FriendshipStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found."));

        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendship.setActionUser(currentUser);
        return friendshipRepository.save(friendship);
    }

    // Từ chối lời mời kết bạn
    @Transactional
    public void rejectFriendRequest(User currentUser, Integer requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found."));

        // Sử dụng phương thức truy vấn mới
        Friendship friendship = friendshipRepository.findByUserOneAndUserTwoAndStatus(requester, currentUser, Friendship.FriendshipStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found."));

        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void unfriend(User currentUser, Integer friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User to unfriend not found."));

        // Tìm mối quan hệ bạn bè đã được chấp nhận giữa hai người
        Friendship friendship = friendshipRepository.findFriendshipBetween(currentUser, friend)
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("You are not friends with this user."));

        // Xóa bản ghi friendship
        friendshipRepository.delete(friendship);
    }

    // Lấy danh sách bạn bè
    public List<FriendResponse> getFriends(User user) {
        return friendshipRepository.findAcceptedFriendships(user).stream()
                .map(friendship -> {
                    // SỬA LẠI LOGIC SO SÁNH BẰNG ID
                    User friend = friendship.getUserOne().getUserId().equals(user.getUserId())
                            ? friendship.getUserTwo()
                            : friendship.getUserOne();

                    return FriendResponse.builder()
                            .userId(friend.getUserId())
                            .username(friend.getDisplayName())
                            .avatarUrl(friend.getAvatarUrl())
                            .build();
                }).collect(Collectors.toList());
    }

    // Lấy danh sách lời mời đã nhận
    public List<FriendRequestResponse> getPendingFriendRequests(User user) {
        return friendshipRepository.findPendingRequestsFor(user).stream()
                .map(friendship -> FriendRequestResponse.builder()
                        .friendshipId(friendship.getFriendshipId())
                        .requesterId(friendship.getUserOne().getUserId())
                        .requesterUsername(friendship.getUserOne().getDisplayName())
                        .requesterAvatarUrl(friendship.getUserOne().getAvatarUrl())
                        .requestDate(friendship.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Hàm helper để xác định trạng thái quan hệ
    private String getFriendshipStatus(User currentUser, User otherUser) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findFriendshipBetween(currentUser, otherUser);
        if (friendshipOpt.isEmpty()) {
            return "NOT_FRIENDS";
        }
        Friendship friendship = friendshipOpt.get();
        switch (friendship.getStatus()) {
            case ACCEPTED:
                return "FRIENDS";
            case PENDING:
                return friendship.getActionUser().getUserId().equals(currentUser.getUserId())
                        ? "REQUEST_SENT"
                        : "REQUEST_RECEIVED";
            default:
                return "NOT_FRIENDS";
        }
    }

    public List<User> getFriendEntities(User user) {
        return friendshipRepository.findAcceptedFriendships(user).stream()
                .map(friendship -> friendship.getUserOne().getUserId().equals(user.getUserId())
                        ? friendship.getUserTwo()
                        : friendship.getUserOne())
                .collect(Collectors.toList());
    }
}