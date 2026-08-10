package com.devtrace.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(
            ServiceException exception,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "order-service",
                exception.getErrorCode().name(),
                exception.getMessage(),
                requestId
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "order-service",
                ErrorCode.INTERNAL_ERROR.name(),
                exception.getMessage(),
                requestId
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
