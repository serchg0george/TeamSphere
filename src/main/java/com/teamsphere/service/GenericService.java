package com.teamsphere.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GenericService<E, D> {

    Page<D> getAll(Pageable pageable);

    D save(D dto);

    D get(Long id);

    void delete(Long id);

    D update(D dto, Long id);

}

