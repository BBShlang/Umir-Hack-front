package com.example.verificationservice.common;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadableBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return buildResponse("Request body is invalid or malformed", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleErrorResponseException(
            ErrorResponseException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        return org.springframework.http.ResponseEntity
                .status(status)
                .body(buildResponse(ex.getBody().getDetail(), status, request));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse("Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ErrorResponse buildResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }
}