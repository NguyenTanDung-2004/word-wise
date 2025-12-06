package com.example.core_word_wise.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchResponse {
    private Integer userId;
    private String username;
    private String avatarUrl;
    // Thêm trạng thái quan hệ để UI biết nên hiển thị nút "Add Friend", "Pending", hay "Friends"
    private String friendshipStatus;
}