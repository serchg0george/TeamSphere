package com.teamsphere.service;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for department operations.
 * Extends GenericService with department-specific functionality.
 */
public interface DepartmentService extends GenericService<DepartmentDto> {

    /**
     * Searches for departments based on search criteria.
     *
     * @param request  the search criteria
     * @param pageable pagination information
     * @return page of matching departments
     */
    Page<DepartmentDto> find(DepartmentSearchRequest request, Pageable pageable);

}
