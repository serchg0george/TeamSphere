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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({NotFoundException.class})
    ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, sqlIntegrityConstraintViolationException.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }


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
