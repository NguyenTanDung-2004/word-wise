package com.example.WordWise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WordWise.dto.request.CreateUserRequest;
import com.example.WordWise.dto.request.LoginRequest;
import com.example.WordWise.entity.User;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.repository.UserRepository;
import com.example.WordWise.utils.JwtUtils;
import com.example.WordWise.utils.PasswordUtils;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordUtils passwordUtils;
    
    public User createUser(CreateUserRequest createUserRequest) {
        String email = createUserRequest.getEmail();
        if (userRepository.findByEmail(email) != null) {
            throw new UserException(EnumException.EMAIL_IS_EXISTED);
        }

        String hashedPassword = passwordUtils.hashPassword(createUserRequest.getPassword());

        User user = User.builder()
                .userName("User_Name")
                .url("default_url")
                .email(email)
                .password(hashedPassword)
                .build();

        userRepository.save(user);
        return user;
    }

    public String genAccessTokenWhenLogin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new UserException(EnumException.USER_NOT_FOUND);
        }

        int checkPassword = passwordUtils.checkPassword(loginRequest.getPassword(), user.getPassword());
        if (checkPassword == 0) {
            throw new UserException(EnumException.PASSWORD_WRONG);
        }

        return jwtUtils.generateJWT(user);
    }

    public void sendCodeViaEmail(String email) {
        
    }
}
