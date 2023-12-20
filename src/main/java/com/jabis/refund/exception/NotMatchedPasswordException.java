package com.jabis.refund.exception;

public class NotMatchedPasswordException extends BusinessException {

    public NotMatchedPasswordException() {
        super(ResponseCode.NOT_MATCHED_PASSWORD);
    }

    public NotMatchedPasswordException(String message) {
        super(ResponseCode.NOT_MATCHED_PASSWORD.getCode(), message);
    }
}
