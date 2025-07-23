package com.example.WordWise.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(value = UserException.class)
    public ResponseEntity handleExceptionUser(UserException userException) {
        return ResponseEntity.status(userException.getEnumException().getHttpStatusCode())
                .body(EnumException.toJson(userException.getEnumException()));
    }
}
