package com.jabis.refund.exception;

public class NotFoundScrapingDataException extends BusinessException {

    public NotFoundScrapingDataException() {
        super(ResponseCode.NOT_FOUND_SCRAPING_DATA);
    }

    public NotFoundScrapingDataException(String message) {
        super(ResponseCode.NOT_FOUND_SCRAPING_DATA.getCode(), message);
    }
}
