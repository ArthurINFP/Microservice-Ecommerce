package com.quangduy.productservice.Utils.Exception;

import com.quangduy.productservice.Utils.Exception.ProductException.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Additional imports
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle ProductNotFoundException
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.error("ProductNotFoundException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handle DuplicateSkuException
    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateSkuException(DuplicateSkuException ex) {
        logger.error("DuplicateSkuException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.CONFLICT.value()));
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Handle InvalidUpdateException
    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUpdateException(InvalidUpdateException ex) {
        logger.error("InvalidUpdateException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handle CategoryNotFoundException
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        logger.error("CategoryNotFoundException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException ex) {
        logger.error("UnauthorizedException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.FORBIDDEN.value()));
        error.put("error", "Unauthorized");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        logger.error("ValidationException: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.put("error", "Bad Request");
        error.put("message", "Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        error.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}