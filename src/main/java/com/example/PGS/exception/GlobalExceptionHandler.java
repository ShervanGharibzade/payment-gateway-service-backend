package com.example.PGS.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // Validation Errors (@Valid)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    // =========================
    // Constraint Violations
    // =========================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    // =========================
    // 404 Not Found
    // =========================
    @ExceptionHandler({MerchantNotFoundException.class, PaymentNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // =========================
    // 403 Forbidden
    // =========================
    @ExceptionHandler(InvalidCallbackSignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSignature(
            InvalidCallbackSignatureException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    // =========================
    // 409 Conflict
    // =========================
    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicatePayment(
            DuplicatePaymentException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    // =========================
    // Generic Business Exceptions (422)
    // =========================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
    }

    // =========================
    // Fallback — FIXED: now logs the real exception
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        // FIXED: log the real exception so it is never silently swallowed
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getRequestURI()
        );
    }

    // =========================
    // Helper
    // =========================
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path
    ) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(path)
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }
}