package com.example.WordWise.mapper;

import org.springframework.stereotype.Component;

import com.example.WordWise.dto.response.UserResponse;
import com.example.WordWise.entity.User;
@Component
public class UserMapper implements Mapper {

    @Override
    public Object map(Object source, Object target) {
        if (source instanceof User && target instanceof UserResponse) {
            return mapToUserResponse((User) source, (UserResponse) target);
        }
        return null;
    }

    public UserResponse mapToUserResponse(User user, UserResponse userResponse) {
        userResponse.setUserId(user.getUserId());
        userResponse.setUserName(user.getUserName());
        userResponse.setEmail(user.getEmail());
        userResponse.setUrl(user.getUrl());
        return userResponse;
    }
    
}
