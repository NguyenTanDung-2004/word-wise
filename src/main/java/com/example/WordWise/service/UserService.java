package com.example.WordWise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WordWise.dto.request.CreateUserRequest;
import com.example.WordWise.dto.request.LoginRequest;
import com.example.WordWise.dto.request.ResetPasswordRequest;
import com.example.WordWise.entity.User;
import com.example.WordWise.enums.EmailTypeEnum;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.model.notification.MailNotification;
import com.example.WordWise.model.notification.MailNotificationProcessor;
import com.example.WordWise.model.notification.Notification;
import com.example.WordWise.model.notification.NotificationFactory;
import com.example.WordWise.model.notification.NotificationProcessor;
import com.example.WordWise.model.notification.NotificationProcessorFactory;
import com.example.WordWise.repository.UserRepository;
import com.example.WordWise.utils.GenUtils;
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

    @Autowired
    private NotificationProcessorFactory notificationProcessorFactory;

    @Autowired
    private NotificationFactory notificationFactory;

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

    public void sendCodeViaEmail(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (user == null) {
            throw new UserException(EnumException.USER_NOT_FOUND);
        }
        String code = GenUtils.generateCodeResetPassword();

        user.setCodeResetPassword(code);
        user = userRepository.save(user);

        MailNotification notification = (MailNotification) notificationFactory.createNotification(MailNotification.class);
        notification.setDataResetPassword(resetPasswordRequest.getEmail(), code, EmailTypeEnum.RESET_PASSWORD);

        NotificationProcessor processor = notificationProcessorFactory.createNotificationProcessor(MailNotificationProcessor.class);
        processor.processNotification(notification);
    }

    public void updateUserPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (user == null) {
            throw new UserException(EnumException.USER_NOT_FOUND);
        }

        if (!user.getCodeResetPassword().equals(resetPasswordRequest.getCode())) {
            throw new UserException(EnumException.CODE_RESET_PASSWORD_WRONG);
        }

        String hashedPassword = passwordUtils.hashPassword(resetPasswordRequest.getPassword());
        user.setPassword(hashedPassword);
        user.setCodeResetPassword(null); // Clear the reset code after successful password change
        userRepository.save(user);
    }
}
