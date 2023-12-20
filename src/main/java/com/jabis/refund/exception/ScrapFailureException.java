package com.jabis.refund.exception;

import lombok.Getter;

@Getter
public class ScrapFailureException extends BusinessException {

    public ScrapFailureException() {
        super(ResponseCode.SCRAP_FAILURE);
    }

    public ScrapFailureException(String message) {
        super(ResponseCode.SCRAP_FAILURE.getCode(), message);
    }

    public ScrapFailureException(Throwable cause) {
        super(ResponseCode.SCRAP_FAILURE, cause);
    }
}
