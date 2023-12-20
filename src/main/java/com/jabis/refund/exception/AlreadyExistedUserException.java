package com.jabis.refund.exception;

public class AlreadyExistedUserException extends BusinessException {

    public AlreadyExistedUserException() {
        super(ResponseCode.ALREADY_EXISTED_USER);
    }

    public AlreadyExistedUserException(String message) {
        super(ResponseCode.ALREADY_EXISTED_USER.getCode(), message);
    }
}
