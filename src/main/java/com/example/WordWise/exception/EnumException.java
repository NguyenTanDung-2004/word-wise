package com.example.WordWise.exception;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EnumException {
    EMAIL_IS_EXISTED("e_01", "Email is existed", HttpStatus.BAD_REQUEST), 
    VERIFY_TOKEN_FAIL("e_02", "Token verification failed", HttpStatus.UNAUTHORIZED), 
    USER_NOT_FOUND("e_03", "User not found", HttpStatus.NOT_FOUND), 
    PASSWORD_WRONG("e_04", "Password is incorrect", HttpStatus.UNAUTHORIZED),
    WORD_NOT_FOUND("e_05", "Word is not found", HttpStatus.NOT_FOUND),
    CODE_RESET_PASSWORD_WRONG("e_06", "Code reset password is incorrect", HttpStatus.BAD_REQUEST),;

    String code;
    String message;
    HttpStatusCode httpStatusCode;

    public static Map<String, Object> toJson(EnumException enumException) {
        return Map.of("code", enumException.getCode(), "message", enumException.getMessage());
    }
}
