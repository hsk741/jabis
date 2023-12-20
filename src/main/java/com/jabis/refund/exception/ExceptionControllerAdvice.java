package com.jabis.refund.exception;

import com.jabis.refund.dto.ErrorResponse;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse<>(ResponseCode.CONSTRAINT_VIOLATION, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<String> businessException(BusinessException businessException) {
        return new ErrorResponse<>(businessException.getCode(), businessException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<String> requestValidationException(ConstraintViolationException e) {
        return new ErrorResponse<>(ResponseCode.CONSTRAINT_VIOLATION, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> dataAccessException(DataAccessException dataAccessException) {
        return new ErrorResponse<>(ResponseCode.DATA_ACCESS_ERROR, dataAccessException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> persistenceException(PersistenceException persistenceException) {
        return new ErrorResponse<>(ResponseCode.SYSTEM_ERROR, persistenceException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> runtimeException(RuntimeException runtimeException) {
        return new ErrorResponse<>(ResponseCode.SYSTEM_ERROR, runtimeException.getMessage());
    }
}
