package com.jabis.refund.exception;

import lombok.Getter;

@Getter
public enum ResponseCode {

    ALREADY_EXISTED_USER(400, "Already sign up user!!!"),

    EXPIRATION_TOKEN(401, "Expired token!!!"),

    NOT_ENABLED_USER(402, "Not enabled user!!!"),

    NOT_FOUND_USER(500, "Not found user!!!"),

    NOT_MATCHED_PASSWORD(501, "Not matched password!!!"),

    SCRAP_FAILURE(502, "Not scrap failure!!!"),

    NOT_FOUND_SCRAPING_DATA(504, "Not found scraping data!!!"),

    SYSTEM_ERROR(505, "System error!!!!"),

    MALFORMED_TOKEN(506, "Bearer token is malformed"),

    DATA_ACCESS_ERROR(800, "Data access error"),

    CONSTRAINT_VIOLATION(900, "Failure the validation");

    private final int code;

    private final String message;

    ResponseCode(int code, String message) {

        this.code = code;
        this.message = message;
    }
}
