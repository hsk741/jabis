package com.jabis.refund.dto;

import com.jabis.refund.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "에러 응답메시지")
@Getter
public class ErrorResponse<T> {

    @Schema(description = "에러코드")
    private final int code;

    @Schema(description = "에러메시지")
    private final String message;

    @Schema(description = "에러세부메시지")
    private T body;

    public ErrorResponse(int code, String message) {

        this.code = code;
        this.message = message;
    }

    public ErrorResponse(int code, String message, T body) {

        this(code, message);
        this.body = body;
    }

    public ErrorResponse(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }

    public ErrorResponse(ResponseCode responseCode, T body) {

        this(responseCode.getCode(), responseCode.getMessage());
        this.body = body;
    }
}

