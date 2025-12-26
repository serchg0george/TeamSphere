package com.teamsphere.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private HttpStatus status;
    private List<SubErrorResponse> errors;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(HttpStatus status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse(HttpStatus httpStatus, List<SubErrorResponse> subErrorResponses) {
        this.status = httpStatus;
        this.errors = subErrorResponses;
    }
}
