package com.buixuanphat.spot_on.exception;


import com.buixuanphat.spot_on.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException e)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.<String>builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<String>> handleAppException(AppException e)
    {
        return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<String>builder().message(e.getMessage()).build());
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException e)
    {
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder().message(e.getFieldError().getDefaultMessage()).build());
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<String>> handleException(Exception e)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<String>builder().message(e.getMessage()).build());
    }
}