package com.guess_game_service.exception;

import com.guess_game_service.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GlobalExceptionHandler {

    // Helper method to build ErrorResponse
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            String errorCode,
            HttpStatus status,
            Exception exception,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errorCode(errorCode)
                .exception(exception.getClass().getSimpleName())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(
                ex.getMessage(),
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                ex,
                request
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("User already exists: {}", ex.getMessage());
        return buildErrorResponse(
                ex.getMessage(),
                "USER_ALREADY_EXISTS",
                HttpStatus.CONFLICT,
                ex,
                request
        );
    }

    @ExceptionHandler(InsufficientTurnsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientTurns(InsufficientTurnsException ex , HttpServletRequest request) {
        log.info("Insufficient turns: {}", ex.getMessage());
        return buildErrorResponse(
                ex.getMessage(),
                "INSUFFICIENT_TURNS",
                HttpStatus.BAD_REQUEST,
                ex,
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials attempt from IP: {}", request.getRemoteAddr());
        return buildErrorResponse(
                "Invalid username or password",
                "INVALID_CREDENTIALS",
                HttpStatus.UNAUTHORIZED,
                ex,
                request
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtError(
            JwtException ex,
            HttpServletRequest request
    ) {
        log.warn("JWT error: {}", ex.getMessage());
        return buildErrorResponse(
                "Invalid or expired token",
                "INVALID_TOKEN",
                HttpStatus.UNAUTHORIZED,
                ex,
                request
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation error on {}: {}", request.getRequestURI(), message);
        return buildErrorResponse(
                message,
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST,
                ex,
                request
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(
                "Invalid or expired token",
                "INVALID_TOKEN",
                HttpStatus.UNAUTHORIZED,
                ex,
                request
        );
    }
}
