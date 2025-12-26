package com.teamsphere.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NotFoundException class.
 */
class NotFoundExceptionTest {

    @Test
    @DisplayName("NotFoundException should contain entity ID in message")
    void constructor_shouldContainEntityIdInMessage() {
        // Given
        Long entityId = 42L;

        // When
        NotFoundException exception = new NotFoundException(entityId);

        // Then
        assertNotNull(exception);
        assertEquals("Entity with id 42 not found.", exception.getMessage());
    }

    @Test
    @DisplayName("NotFoundException should extend RuntimeException")
    void exception_shouldExtendRuntimeException() {
        // Given
        NotFoundException exception = new NotFoundException(1L);

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("NotFoundException message should be formatted correctly for different IDs")
    void message_shouldBeFormattedCorrectly() {
        // Test with various IDs
        assertEquals("Entity with id 0 not found.", new NotFoundException(0L).getMessage());
        assertEquals("Entity with id 1 not found.", new NotFoundException(1L).getMessage());
        assertEquals("Entity with id 999999 not found.", new NotFoundException(999999L).getMessage());
    }
}

