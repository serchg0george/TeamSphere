package com.teamsphere.service;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for employee operations.
 * Extends GenericService with employee-specific functionality.
 */
public interface EmployeeService extends GenericService<EmployeeDto> {

    /**
     * Searches for employees based on search criteria.
     *
     * @param request  the search criteria
     * @param pageable pagination information
     * @return page of matching employees
     */
    Page<EmployeeDto> find(EmployeeSearchRequest request, Pageable pageable);

}
