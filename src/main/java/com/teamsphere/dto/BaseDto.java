package com.teamsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Base DTO class containing common fields for all DTOs.
 * Provides ID, creation timestamp, and update timestamp fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDto {
    /**
     * Unique identifier.
     */
    private Long id;
    
    /**
     * Timestamp when the entity was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the entity was last updated.
     */
    private LocalDateTime updatedAt;
}
