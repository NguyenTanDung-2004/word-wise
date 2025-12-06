package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.post.CreatePostRequest;
import com.example.core_word_wise.dto.post.PostResponse;
import com.example.core_word_wise.dto.user.UserResponse;
import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.entity.Post;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.PostLikeRepository;
import com.example.core_word_wise.repository.SavedCollectionRepository;
import com.example.core_word_wise.repository.UserWordRepository;
import com.example.core_word_wise.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserWordRepository userWordRepository;
    private final PostLikeRepository postLikeRepository;
    private final SavedCollectionRepository savedCollectionRepository;

    // Tạo bài đăng mới
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal User author,
            @Valid @RequestBody CreatePostRequest request
    ) {
        Post newPost = postService.createPost(author, request);
        PostResponse response = mapToPostResponse(newPost, author, false); // Mới tạo, chưa thể like
        return new ResponseEntity<>(ApiResponse.success(response, "Post created successfully."), HttpStatus.CREATED);
    }

    // Like/Unlike một bài đăng
    @PostMapping("/{postId}/toggle-like")
    public ResponseEntity<ApiResponse<PostResponse>> toggleLike(
            @AuthenticationPrincipal User user,
            @PathVariable Integer postId
    ) {
        Post updatedPost = postService.toggleLike(user, postId);
        // Kiểm tra lại xem sau khi toggle, user có còn like post không
        boolean isLiked = postLikeRepository.findByUserAndPost(user, updatedPost).isPresent();
        PostResponse response = mapToPostResponse(updatedPost, user, isLiked);
        return ResponseEntity.ok(ApiResponse.success(response, "Like status toggled."));
    }

    // Hàm helper để chuyển từ Entity sang DTO
    private PostResponse mapToPostResponse(Post post, User currentUser, boolean isLiked) {
        UserResponse authorDto = UserResponse.builder()
                .userId(post.getUser().getUserId())
                .username(post.getUser().getDisplayName())
                .avatarUrl(post.getUser().getAvatarUrl())
                .build();

        PostResponse.CollectionInfo collectionInfo = null;
        if (post.getCollection() != null) {
            Collection sharedCollection = post.getCollection();
            User postAuthor = post.getUser(); // Tác giả bài post

            // 1. Kiểm tra xem người dùng hiện tại (currentUser) đã lưu collection này chưa
            boolean isSaved = savedCollectionRepository.existsByUserAndOriginalCollection(currentUser, sharedCollection);

            // 2. Tính Word Count: Đếm số từ mà TÁC GIẢ BÀI POST đã lưu trong collection này.
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyPosts(@AuthenticationPrincipal User user) {
        List<Post> posts = postService.getPostsByUser(user);
        // Cần chuyển đổi sang DTO
        List<PostResponse> response = posts.stream()
                .map(post -> mapToPostResponse(post, user, postLikeRepository.findByUserAndPost(user, post).isPresent()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Your posts retrieved successfully."));
    }
}