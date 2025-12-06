package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.post.PostResponse;
import com.example.core_word_wise.dto.user.UserResponse;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.repository.PostLikeRepository;
import com.example.core_word_wise.repository.PostRepository;
import com.example.core_word_wise.repository.SavedCollectionRepository;
import com.example.core_word_wise.repository.UserWordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final FriendshipService friendshipService;
    private final PostLikeRepository postLikeRepository;
    private final UserWordRepository userWordRepository;
    private final SavedCollectionRepository savedCollectionRepository;


    // Lấy News Feed (bài của bạn bè và của chính mình)
    public List<PostResponse> getNewsFeed(User currentUser) {
        // Lấy danh sách bạn bè
        List<User> friends = friendshipService.getFriendEntities(currentUser);

        // Thêm chính mình vào danh sách
        List<User> usersForFeed = new ArrayList<>(friends);
        usersForFeed.add(currentUser);

        // Lấy tất cả các bài post từ danh sách người dùng này, sắp xếp theo thời gian mới nhất
        List<Post> posts = postRepository.findByUserIn(usersForFeed, Sort.by(Sort.Direction.DESC, "createdAt"));

        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    // Lấy danh sách thông báo về collection được chia sẻ
    public List<PostResponse> getSharedCollectionNotifications(User currentUser) {
        // Lấy danh sách bạn bè
        List<User> friends = friendshipService.getFriendEntities(currentUser);

        // Lấy các bài post là dạng share collection từ bạn bè, sắp xếp mới nhất
        List<Post> posts = postRepository.findSharedCollectionPostsFromUsers(friends, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Chuyển đổi sang DTO
        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveSharedCollection(User user, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found."));

        Collection collectionToSave = post.getCollection();
        if (collectionToSave == null) {
            throw new IllegalArgumentException("This post does not share a collection.");
        }

        // So sánh bằng ID để tránh lỗi detached entity
        if (collectionToSave.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You cannot save your own collection.");
        }

        if (savedCollectionRepository.existsByUserAndOriginalCollection(user, collectionToSave)) {
            throw new IllegalArgumentException("You have already saved this collection.");
        }

        // 1. Lưu bản ghi `SavedCollection`
        SavedCollection savedCollection = new SavedCollection();
        savedCollection.setUser(user);
        savedCollection.setOriginalCollection(collectionToSave);
        savedCollection.setCreatedAt(LocalDateTime.now());
        savedCollectionRepository.save(savedCollection);

        // 2. Lấy tất cả các UserWord của collection gốc
        List<UserWord> originalUserWords = userWordRepository.findAllByCollection_CollectionId(collectionToSave.getCollectionId());

        // 3. Tạo các UserWord mới cho người dùng hiện tại
        List<UserWord> newUserWords = originalUserWords.stream()
                .filter(originalUW -> originalUW.getUser().getUserId().equals(collectionToSave.getUser().getUserId())) // Chỉ sao chép từ người tạo gốc
                .map(originalUW -> {
                    UserWord newUserWord = new UserWord();
                    newUserWord.setUser(user);
                    newUserWord.setWord(originalUW.getWord());
                    newUserWord.setCollection(collectionToSave);
                    newUserWord.setFamiliarityScore(0.0f);
                    newUserWord.setReviewCount(0);
                    newUserWord.setNextReviewDate(LocalDate.now());
                    newUserWord.setPriorityLevel(UserWord.PriorityLevel.MEDIUM);
                    return newUserWord;
                }).collect(Collectors.toList());

        // 4. Lưu tất cả các UserWord mới
        if (!newUserWords.isEmpty()) {
            userWordRepository.saveAll(newUserWords);
        }

    }

    // Hàm helper để chuyển đổi Post sang PostResponse (có thể đặt ở service riêng nếu muốn)
    private PostResponse mapToPostResponse(Post post, User currentUser) {
        UserResponse authorDto = UserResponse.builder()
                .userId(post.getUser().getUserId())
                .username(post.getUser().getDisplayName())
                .avatarUrl(post.getUser().getAvatarUrl())
                .build();

        PostResponse.CollectionInfo collectionInfo = null;
        if (post.getCollection() != null) {
            Collection sharedCollection = post.getCollection();
            User postAuthor = post.getUser();

            boolean isSaved = savedCollectionRepository.existsByUserAndOriginalCollection(currentUser, sharedCollection);

            // SỬA: Đếm số từ mà TÁC GIẢ BÀI POST đã lưu trong collection này.
            Long wordCount = userWordRepository.countByUserAndCollection_CollectionId(
                    postAuthor,
                    sharedCollection.getCollectionId()
            );

            collectionInfo = PostResponse.CollectionInfo.builder()
                    .collectionId(sharedCollection.getCollectionId())
                    .name(sharedCollection.getName())
                    .wordCount(wordCount)
                    .isSavedByCurrentUser(isSaved)
                    .build();
        }

        // Kiểm tra xem người dùng hiện tại đã like bài post này chưa
        boolean isLiked = postLikeRepository.findByUserAndPost(currentUser, post).isPresent();

        return PostResponse.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .author(authorDto)
                .collectionInfo(collectionInfo)
                .likesCount(post.getLikesCount())
                .isLikedByCurrentUser(isLiked)
                .createdAt(post.getCreatedAt())
                .build();
    }
}