package com.jabis.refund.exception;

public class NotEnabledUserException extends BusinessException {

    public NotEnabledUserException() {
        super(ResponseCode.NOT_ENABLED_USER);
    }

    public NotEnabledUserException(String message) {
        super(ResponseCode.NOT_ENABLED_USER.getCode(), message);
    }
}
