package com.teamsphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application.
 * Handles various exceptions and returns appropriate error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles NotFoundException and returns a NOT_FOUND response.
     *
     * @param e the NotFoundException that was thrown
     * @return ResponseEntity containing the error response with NOT_FOUND status
     */
    @ExceptionHandler({NotFoundException.class})
    ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles SQL integrity constraint violations and returns a CONFLICT response.
     *
     * @param sqlIntegrityConstraintViolationException the SQL constraint violation exception
     * @return ResponseEntity containing the error response with CONFLICT status
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, sqlIntegrityConstraintViolationException.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles validation exceptions and returns a BAD_REQUEST response with field-level errors.
     *
     * @param methodArgumentNotValidException the validation exception containing field errors
     * @return ResponseEntity containing the error response with BAD_REQUEST status and validation errors
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {

        List<SubErrorResponse> subErrorResponses = new ArrayList<>();
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            subErrorResponses.add(new SubErrorResponse(LocalDateTime.now(),
                    fieldError.getDefaultMessage(), fieldError.getField()));
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, subErrorResponses);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

}
