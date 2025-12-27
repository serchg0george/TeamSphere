package com.teamsphere.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Generic service interface providing common CRUD operations.
 *
 * @param <D> the DTO type
 */
public interface GenericService<D> {

    /**
     * Retrieves all entities with pagination.
     *
     * @param pageable pagination information
     * @return page of DTOs
     */
    Page<D> getAll(Pageable pageable);

    /**
     * Saves a new entity.
     *
     * @param dto the DTO to save
     * @return the saved DTO
     */
    D save(D dto);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the entity ID
     * @return the DTO
     * @throws com.teamsphere.exception.NotFoundException if entity not found
     */
    D get(Long id);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID to delete
     * @throws com.teamsphere.exception.NotFoundException if entity not found
     */
    void delete(Long id);

    /**
     * Updates an existing entity.
     *
     * @param dto the DTO with updated data
     * @param id  the entity ID to update
     * @return the updated DTO
     * @throws com.teamsphere.exception.NotFoundException if entity not found
     */
    D update(D dto, Long id);

}

