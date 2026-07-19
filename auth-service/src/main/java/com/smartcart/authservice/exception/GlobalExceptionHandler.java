package com.smartcart.authservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception caught: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Runtime exception: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(feign.FeignException.class)
    public final ResponseEntity<ErrorResponse> handleFeignExceptions(feign.FeignException ex, WebRequest request) {
        log.error("Feign exception caught: status={}, message={}", ex.status(), ex.getMessage());
        
        String cleanMessage = ex.getMessage();
        String content = ex.contentUTF8();
        if (content != null && !content.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(content);
                if (jsonNode.has("message")) {
                    cleanMessage = jsonNode.get("message").asText();
                }
            } catch (Exception e) {
                log.error("Failed to parse Feign error content JSON", e);
            }
        }
        
        int statusCode = ex.status() > 0 ? ex.status() : HttpStatus.BAD_REQUEST.value();
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                cleanMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
