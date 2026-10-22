package com.posadskiy.skillrepeater.web.exception;

import com.posadskiy.skillrepeater.core.exception.AuthException;
import com.posadskiy.skillrepeater.core.exception.ReviewConflictException;
import com.posadskiy.skillrepeater.core.exception.SkillNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(classes = {Exception.class})
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleAuthException(AuthException ex, HttpRequest request) {
        logger.error("Authentication error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            "AUTH_ERROR",
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleBusinessException(BusinessException ex, HttpRequest request) {
        logger.error("Business logic error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            ex.getErrorCode(),
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleValidationException(ConstraintViolationException ex, HttpRequest request) {
        logger.error("Validation error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            "Validation failed: " + ex.getMessage(),
            "VALIDATION_ERROR",
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpRequest request) {
        logger.error("Illegal argument error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            "Invalid request: " + ex.getMessage(),
            "INVALID_ARGUMENT",
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleSkillNotFound(SkillNotFoundException ex, HttpRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "NOT_FOUND", request.getPath());
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleReviewConflict(ReviewConflictException ex, HttpRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "CONFLICT", request.getPath());
        return HttpResponse.status(HttpStatus.CONFLICT).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpRequest request) {
        logger.error("Runtime error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            "Internal server error occurred",
            "RUNTIME_ERROR",
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleException(Exception ex, HttpRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
            "An unexpected error occurred",
            "UNEXPECTED_ERROR",
            request.getPath()
        );

        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 
