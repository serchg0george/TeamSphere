package com.teamsphere.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Sub-error response object for detailed field-level validation errors.
 * Contains timestamp, error message, and the field that caused the error.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
public class SubErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Error message describing the validation failure.
     */
    private String message;
    
    /**
     * Field name that caused the validation error.
     */
    private String field;

}
