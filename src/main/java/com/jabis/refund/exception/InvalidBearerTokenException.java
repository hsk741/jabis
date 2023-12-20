package com.jabis.refund.exception;

public class InvalidBearerTokenException extends BusinessException {

    public InvalidBearerTokenException() {
        super(ResponseCode.MALFORMED_TOKEN);
    }

    public InvalidBearerTokenException(String message) {
        super(ResponseCode.MALFORMED_TOKEN.getCode(), message);
    }
}
