package com.teamsphere.mapper.base;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    void updateFromDto(D dto, E entity);
}
