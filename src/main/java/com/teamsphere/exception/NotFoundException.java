package com.teamsphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested entity is not found.
 * Returns HTTP 404 NOT_FOUND status.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
     * Constructs a NotFoundException with the entity ID.
     *
     * @param id the ID of the entity that was not found
     */
    public NotFoundException(Long id) {
        super("Entity with id " + id + " not found.");
    }
}
