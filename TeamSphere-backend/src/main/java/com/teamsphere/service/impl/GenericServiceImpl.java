package com.teamsphere.service.impl;

import com.teamsphere.dto.BaseDto;
import com.teamsphere.entity.BaseEntity;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.service.GenericService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class GenericServiceImpl<E extends BaseEntity, D extends BaseDto> implements GenericService<E, D> {
    public abstract BaseMapper<E, D> getMapper();

    public abstract JpaRepository<E, Long> getRepository();

    @Override
    public Page<D> getAll(Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(entity -> getMapper().toDto(entity));
    }

    @Override
    @Transactional
    public D save(D dto) {
        E entityForSave = getRepository().save(getMapper().toEntity(dto));
        return getMapper().toDto(entityForSave);
    }

    @Override
    public D get(Long id) {
        E entity = getRepository().findById(id).orElseThrow(() -> new NotFoundException(id));
        return getMapper().toDto(entity);
    }

    @Override
    public void delete(Long id) {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
        } else {
            throw new NotFoundException(id);
        }
    }

    @Override
    @Transactional
    public D update(D dto, Long id) {
        E entityDb = getRepository().findById(id).orElseThrow(() -> new NotFoundException(id));
        getMapper().updateFromDto(dto, entityDb);
        return getMapper().toDto(getRepository().save(entityDb));
    }
}
