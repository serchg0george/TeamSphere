package com.teamsphere.mapper.base;

/**
 * Base mapper interface for converting between entities and DTOs.
 *
 * @param <E> the entity type
 * @param <D> the DTO type
 */
public interface BaseMapper<E, D> {
    /**
     * Converts an entity to a DTO.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    D toDto(E entity);
    
    /**
     * Converts a DTO to an entity.
     *
     * @param dto the DTO to convert
     * @return the converted entity
     */
    E toEntity(D dto);
    
    /**
     * Updates an entity from a DTO.
     *
     * @param dto the DTO containing updated data
     * @param entity the entity to update
     */
    void updateFromDto(D dto, E entity);
}
