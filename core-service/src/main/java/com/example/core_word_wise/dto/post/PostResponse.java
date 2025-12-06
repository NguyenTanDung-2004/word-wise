package com.example.core_word_wise.dto.post;

import com.example.core_word_wise.dto.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Integer postId;
    private String content;
    private UserResponse author;
    private CollectionInfo collectionInfo;
    private Integer likesCount;
    private boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class CollectionInfo {
        private Integer collectionId;
        private String name;
        private Long wordCount;
        private boolean isSavedByCurrentUser;
    }
}