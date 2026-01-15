package com.rewardplus.loyalty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for consistent response format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private LocalDateTime timestamp;
    private PaginationInfo pagination;

    /**
     * Create a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Create a successful response with data and message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Create a successful response with message only.
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Create an error response.
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(ErrorDetails.builder()
                .message(message)
                .code(errorCode)
                .build())
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Create an error response with details.
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, String details) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(ErrorDetails.builder()
                .message(message)
                .code(errorCode)
                .details(details)
                .build())
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Create a paginated response.
     */
    public static <T> ApiResponse<T> paginated(T data, int page, int size, long totalElements) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .pagination(PaginationInfo.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .build())
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Error details embedded object.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String message;
        private String code;
        private String details;
    }

    /**
     * Pagination information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public static PaginationInfo of(int page, int size, long totalElements) {
            int totalPages = (int) Math.ceil((double) totalElements / size);
            return PaginationInfo.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
        }
    }
}

