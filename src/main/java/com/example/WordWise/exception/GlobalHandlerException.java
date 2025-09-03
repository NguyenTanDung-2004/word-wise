package com.example.WordWise.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(value = UserException.class)
    public ResponseEntity handleExceptionUser(UserException userException) {
        return ResponseEntity.status(userException.getEnumException().getHttpStatusCode())
                .body(EnumException.toJson(userException.getEnumException()));
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Map<String, Object>> handleRuntimeException(
//            RuntimeException exception,
//            HttpServletRequest request
//    ) {
//        Map<String, Object> errorResponse = Map.of(
//                "timestamp", LocalDateTime.now().toString(),
//                "statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                "exception", exception.getClass().getSimpleName(),
//                "errorMessage", exception.getMessage(),
//                "path", request.getRequestURI()
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }

}