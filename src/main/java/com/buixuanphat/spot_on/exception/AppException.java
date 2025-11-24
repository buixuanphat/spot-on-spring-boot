package com.buixuanphat.spot_on.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppException extends RuntimeException {
    ErrorMessage errorMessage;

    public AppException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
