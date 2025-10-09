package com.teamsphere.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private NotFoundException notFoundException;
    private SQLIntegrityConstraintViolationException sqlException;
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @BeforeEach
    void setUp() {
        notFoundException = new NotFoundException(1L);
        sqlException = new SQLIntegrityConstraintViolationException("Duplicate entry");
    }

    @Test
    void handleNotFoundException_shouldReturnNotFoundResponse() {
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(notFoundException);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Entity with id 1 not found"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void onSQLIntegrityConstraintViolation_shouldReturnConflictResponse() {
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.onSQLIntegrityConstraintViolation(sqlException);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT, response.getBody().getStatus());
        assertEquals("Duplicate entry", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void onMethodArgumentNotValidException_shouldReturnBadRequestWithFieldErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name is required");
        FieldError fieldError2 = new FieldError("object", "email", "Email is invalid");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        methodArgumentNotValidException = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.onMethodArgumentNotValidException(methodArgumentNotValidException);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertNotNull(response.getBody().getErrors());
        assertEquals(2, response.getBody().getErrors().size());
        
        SubErrorResponse error1 = response.getBody().getErrors().get(0);
        assertEquals("name", error1.getField());
        assertEquals("Name is required", error1.getMessage());
        assertNotNull(error1.getTimestamp());
        
        SubErrorResponse error2 = response.getBody().getErrors().get(1);
        assertEquals("email", error2.getField());
        assertEquals("Email is invalid", error2.getMessage());
        assertNotNull(error2.getTimestamp());
    }

    @Test
    void onMethodArgumentNotValidException_withEmptyErrors_shouldReturnBadRequest() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        
        methodArgumentNotValidException = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.onMethodArgumentNotValidException(methodArgumentNotValidException);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
}

