package com.artventuria.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("Validation failed for request {} - Fields: {}",
                request.getDescription(false),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid credentials");

        logger.warn("Authentication failed - Bad credentials for request {}",
                request.getDescription(false));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Authentication failed");

        logger.warn("Authentication failed for request {} - {}",
                request.getDescription(false),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        logger.warn("Validation error for request {} - {}",
                request.getDescription(false),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        logger.warn("Email already exists error for request {} - {}",
                request.getDescription(false),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        logger.warn("Resource not found for request {} - {}",
                request.getDescription(false),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Method not supported: " + ex.getMessage());
        error.put("supportedMethods", String.join(", ", ex.getSupportedMethods()));

        logger.warn("Method not supported for request {} - {}",
                request.getDescription(false),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(
            Exception ex,
            WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred");

        logger.error("Unhandled exception for request {} - {}",
                request.getDescription(false),
                ex.getMessage(),
                ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}