package com.example.WordWise.dto.response;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EnumResponse {
    DONE("s_0", "DONE", HttpStatus.OK),
    CREATE_USER_SUCCESS("s_01", "Create user successfully", HttpStatus.OK),
    LOGIN_SUCCESS("s_02", "Login successfully", HttpStatus.OK),
    SEND_CODE_FORGOT_PASSWORD_SUCCESS("s_03", "Send code forgot password successfully", HttpStatus.OK),
    RESET_PASSWORD_SUCCESS("s_04", "Reset password successfully!", HttpStatus.OK),
    GET_USERINFO_SUCCESS("s_05", "Get user info successfully!", HttpStatus.OK),
    UPDATE_BASIC_USERINFO("s_06", "Update user info successfully!", HttpStatus.OK),
    UPDATE_USER_HOBBIES("s_07", "Update user hobbies successfully!", HttpStatus.OK),
    SEARCH_USER_SUCCESS("s_08", "Search user successfully!", HttpStatus.OK),
    INSERT_WORD_SUCCESS("s_09", "Insert word successfully!", HttpStatus.OK),
    EDIT_WORD_SUCCESS("s_10", "Edit word successfully!", HttpStatus.OK),
    GET_WORDS_SUCCESS("s_11", "Get words successfully!", HttpStatus.OK),
    GET_NEWFEED_SUCCESS("s_12", "Get new feeds successfully!", HttpStatus.OK);

    String code;
    String message;
    HttpStatusCode httpStatus;

    public static Map<String, Object> toJson(EnumResponse enumResponse) {
        return Map.of("code", enumResponse.getCode(), "message", enumResponse.getMessage());
    }
}
