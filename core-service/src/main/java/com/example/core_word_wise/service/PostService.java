package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.post.CreatePostRequest;
import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.entity.Post;
import com.example.core_word_wise.entity.PostLike;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.repository.CollectionRepository;
import com.example.core_word_wise.repository.PostLikeRepository;
import com.example.core_word_wise.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;
    private final PostLikeRepository postLikeRepository;

    // Tạo bài đăng mới
    @Transactional
    public Post createPost(User author, CreatePostRequest request) {
        Post post = new Post();
        post.setUser(author);
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setLikesCount(0);

        if (request.getCollectionName() != null && !request.getCollectionName().isBlank()) {
            // 1. Tìm collection
            Collection collectionToShare = collectionRepository.findByNameAndUserAndIsDeletedFalse(request.getCollectionName(), author)
                    .orElseThrow(() -> new EntityNotFoundException("Collection '" + request.getCollectionName() + "' not found or you don't have permission to share it."));

            // 2.  Đánh dấu là public để người khác có thể truy cập
            collectionToShare.setIsPublic(true);
            collectionRepository.save(collectionToShare); // Lưu lại thay đổi

            // 3. Liên kết post với collection
            post.setCollection(collectionToShare);
        }

        return postRepository.save(post);
    }

    // Like hoặc Unlike một bài đăng
    @Transactional
    public Post toggleLike(User user, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // Đã like -> Unlike
            postLikeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            // Chưa like -> Like
            PostLike newLike = new PostLike();
            newLike.setUser(user);
            newLike.setPost(post);
            newLike.setCreatedAt(LocalDateTime.now());
            postLikeRepository.save(newLike);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        return postRepository.save(post);
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}