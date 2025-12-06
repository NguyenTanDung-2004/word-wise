package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.Post;
import com.example.core_word_wise.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    // Tìm tất cả các bài post của một danh sách người dùng (dùng cho news feed)
    List<Post> findByUserIn(List<User> users, Sort sort);

    // Tìm tất cả các bài post của một người dùng cụ thể
    List<Post> findByUser(User user, Sort sort);

    // Tìm các bài post là dạng share collection từ một danh sách người dùng (dùng cho thông báo)
    @Query("SELECT p FROM Post p WHERE p.user IN :users AND p.collection IS NOT NULL")
    List<Post> findSharedCollectionPostsFromUsers(List<User> users, Sort sort);
}