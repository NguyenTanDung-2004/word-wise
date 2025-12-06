package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Friendship;
import com.example.core_word_wise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    // Tìm một mối quan hệ giữa hai người dùng, bất kể thứ tự
    @Query("SELECT f FROM Friendship f WHERE (f.userOne = :user1 AND f.userTwo = :user2) OR (f.userOne = :user2 AND f.userTwo = :user1)")
    Optional<Friendship> findFriendshipBetween(User user1, User user2);

    // Tìm tất cả các mối quan hệ bạn bè đã được chấp nhận của một người dùng
    @Query("SELECT f FROM Friendship f WHERE (f.userOne = :user OR f.userTwo = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(User user);

    // Tìm tất cả các lời mời đang chờ mà người dùng này nhận được
    @Query("SELECT f FROM Friendship f WHERE f.userTwo = :user AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsFor(User user);

    Optional<Friendship> findByUserOneAndUserTwoAndStatus(User requester, User receiver, Friendship.FriendshipStatus status);
}