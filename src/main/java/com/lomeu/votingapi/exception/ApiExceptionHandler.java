package com.lomeu.votingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        ErrorCode errorCode = errors.containsKey("durationInSeconds")
                ? ErrorCode.INVALID_DURATION
                : ErrorCode.VALIDATION_ERROR;
        if (errors.containsKey("vote")) {
            errorCode = ErrorCode.INVALID_VOTE;
        }
        return errorResponse(errorCode.status(), errorCode.code(), errorCode.message(), errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage() {
        return errorResponse(
                ErrorCode.VALIDATION_ERROR.status(),
                ErrorCode.VALIDATION_ERROR.code(),
                "Request body is invalid.",
                Map.of()
        );
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingPathVariableException.class})
    public ResponseEntity<ApiError> handleInvalidParameter(Exception exception) {
        return errorResponse(
                ErrorCode.VALIDATION_ERROR.status(),
                ErrorCode.VALIDATION_ERROR.code(),
                "Request parameter is invalid.",
                null
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType() {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.code(),
                "Request content type is not supported.",
                null
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessError(BusinessException exception) {
        ErrorCode errorCode = exception.errorCode();
        log.warn("Business error: {}", errorCode.code());

        Map<String, String> errors = switch (errorCode) {
            case INVALID_DURATION -> Map.of("durationInSeconds", errorCode.message());
            case INVALID_VOTE -> Map.of("vote", errorCode.message());
            default -> null;
        };

        return errorResponse(
                errorCode.status(),
                errorCode.code(),
                errorCode.message(),
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception) {
        log.error("Unexpected API error", exception);
        return errorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.status(),
                ErrorCode.INTERNAL_SERVER_ERROR.code(),
                ErrorCode.INTERNAL_SERVER_ERROR.message(),
                null
        );
    }

    private ResponseEntity<ApiError> errorResponse(
            HttpStatus status,
            String code,
            String message,
            Map<String, String> errors
    ) {
        ApiError body = new ApiError(
                status.value(),
                code,
                message,
                Instant.now(clock),
                errors
        );
        return ResponseEntity.status(status).body(body);
    }
}
