package com.jabis.refund.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }

    public BusinessException(int code, String message, Throwable cause) {

        super(message, cause);
        this.code = code;
    }

    public BusinessException(ResponseCode responseCode, Throwable cause) {

        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
    }
}
