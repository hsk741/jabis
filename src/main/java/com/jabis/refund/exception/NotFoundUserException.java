package com.jabis.refund.exception;

public class NotFoundUserException extends BusinessException {

    public NotFoundUserException() {
        super(ResponseCode.NOT_FOUND_USER);
    }

    public NotFoundUserException(String message) {
        super(ResponseCode.NOT_FOUND_USER.getCode(), message);
    }
}
