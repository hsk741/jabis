package com.jabis.refund.exception;

public class ExpiredTokenException extends BusinessException {

    public ExpiredTokenException() {
        super(ResponseCode.EXPIRATION_TOKEN);
    }

    public ExpiredTokenException(String message) {
        super(ResponseCode.EXPIRATION_TOKEN.getCode(), message);
    }
}
