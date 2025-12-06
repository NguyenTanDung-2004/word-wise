package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Post;
import com.example.core_word_wise.entity.PostLike;
import com.example.core_word_wise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.core_word_wise.entity.ids.PostLikeId;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId >  {
    Optional<PostLike> findByUserAndPost(User user, Post post);
}