package com.example.core_word_wise.entity;

import com.example.core_word_wise.entity.ids.PostLikeId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_like")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostLikeId.class)
public class PostLike {
    @Id
    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createdAt;
}