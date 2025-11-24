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
    ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e)
    {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.status(ErrorMessage.UNAUTHORIZE.getCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException e)
    {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.status(e.getErrorMessage().getCode()).body(apiResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e)
    {
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        ErrorMessage errorMessage = ErrorMessage.valueOf(enumKey);
        apiResponse.setMessage(errorMessage.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handleException(Exception e)
    {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
