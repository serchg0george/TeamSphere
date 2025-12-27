package com.teamsphere.service.impl;

import com.teamsphere.dto.BaseDto;
import com.teamsphere.entity.BaseEntity;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.service.GenericService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Abstract base implementation of GenericService providing common CRUD operations.
 *
 * @param <D> the DTO type extending BaseDto
 */
public abstract class GenericServiceImpl<E extends BaseEntity, D extends BaseDto> implements GenericService<D> {
    /**
     * Gets the mapper for converting between entity and DTO.
     *
     * @return the mapper instance
     */
    public abstract BaseMapper<E, D> getMapper();

    /**
     * Gets the repository for database operations.
     *
     * @return the repository instance
     */
    public abstract JpaRepository<E, Long> getRepository();

    @Override
    public Page<D> getAll(Pageable pageable) {

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id"))
        );

        return getRepository().findAll(sorted)
                .map(entity -> getMapper().toDto(entity));
    }

    /**
     * Saves a new entity to the database.
     *
     * @param dto the DTO to save
     * @return the saved DTO
     */
    @Override
    @Transactional
    public D save(D dto) {
        E entityForSave = getRepository().save(getMapper().toEntity(dto));
        entityForSave.setCreatedAt(LocalDateTime.now());
        return getMapper().toDto(entityForSave);
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the entity ID
     * @return the DTO
     * @throws NotFoundException if entity not found
     */
    @Override
    public D get(Long id) {
        E entity = getRepository().findById(id).orElseThrow(() -> new NotFoundException(id));
        return getMapper().toDto(entity);
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID to delete
     * @throws NotFoundException if entity not found
     */
    @Override
    public void delete(Long id) {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
        } else {
            throw new NotFoundException(id);
        }
    }

    /**
     * Updates an existing entity.
     *
     * @param dto the DTO with updated data
     * @param id  the entity ID to update
     * @return the updated DTO
     * @throws NotFoundException if entity not found
     */
    @Override
    @Transactional
    public D update(D dto, Long id) {
        E entityDb = getRepository().findById(id).orElseThrow(() -> new NotFoundException(id));
        getMapper().updateFromDto(dto, entityDb);
        entityDb.setUpdatedAt(LocalDateTime.now());
        return getMapper().toDto(getRepository().save(entityDb));
    }
}
