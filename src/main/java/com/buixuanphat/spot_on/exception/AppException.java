package com.buixuanphat.spot_on.exception;

import lombok.Data;

@Data
public class AppException extends RuntimeException {
    int statusCode;
    String message;

    public AppException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }
}
