package ru.splendidpdf.api;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ErrorInfo handleRuntimeException(RuntimeException exception) {
        return ErrorInfo.createErrorInfo(exception);
    }

    @Value
    @Builder
    private static class ErrorInfo {
        UUID errorId;
        String message;
        String timestamp;
        Throwable cause;

        static ErrorInfo createErrorInfo(RuntimeException exception) {
            return ErrorInfo.builder()
                    .errorId(UUID.randomUUID())
                    .cause(exception.getCause())
                    .message(exception.getMessage())
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }
}
