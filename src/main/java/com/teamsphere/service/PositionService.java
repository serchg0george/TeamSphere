package com.teamsphere.service;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for position operations.
 * Extends GenericService with position-specific functionality.
 */
public interface PositionService extends GenericService<PositionDto> {

    /**
     * Searches for positions based on search criteria.
     *
     * @param searchRequest the search criteria
     * @param pageable      pagination information
     * @return page of matching positions
     */
    Page<PositionDto> find(PositionSearchRequest searchRequest, Pageable pageable);

}
