package com.rewardplus.loyalty.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for API errors.
 * Provides consistent error format across all API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private Map<String, Object> details;

    /**
     * Creates an error response with timestamp.
     */
    public static ErrorResponse of(int status, String error, String message, String errorCode) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .errorCode(errorCode)
            .build();
    }

    /**
     * Creates an error response with details.
     */
    public static ErrorResponse withDetails(int status, String error, String message, 
                                           String errorCode, Map<String, Object> details) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .errorCode(errorCode)
            .details(details)
            .build();
    }
}

