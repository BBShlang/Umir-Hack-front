package com.example.issuerservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessConflictException.class)
    public ProblemDetail handleBusinessConflict(BusinessConflictException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Business conflict");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getRequestURI());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Validation error");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getRequestURI());

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
        problemDetail.setTitle("Internal server error");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getRequestURI());
        return problemDetail;
    }
}