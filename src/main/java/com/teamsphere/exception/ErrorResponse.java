package com.teamsphere.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response object for error information.
 * Contains HTTP status, error details, message, timestamp, and path.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private HttpStatus status;
    private List<SubErrorResponse> errors;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    /**
     * Constructs an ErrorResponse with status, message, and timestamp.
     *
     * @param status the HTTP status
     * @param message the error message
     * @param timestamp the timestamp when the error occurred
     */
    public ErrorResponse(HttpStatus status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Constructs an ErrorResponse with status and list of sub-errors.
     *
     * @param httpStatus the HTTP status
     * @param subErrorResponses list of detailed sub-errors
     */
    public ErrorResponse(HttpStatus httpStatus, List<SubErrorResponse> subErrorResponses) {
        this.status = httpStatus;
        this.errors = subErrorResponses;
    }
}
