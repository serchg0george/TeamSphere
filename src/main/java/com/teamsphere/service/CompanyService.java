package com.teamsphere.service;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for company operations.
 * Extends GenericService with company-specific functionality.
 */
public interface CompanyService extends GenericService<CompanyDto> {

    /**
     * Searches for companies based on search criteria.
     *
     * @param request the search criteria
     * @param pageable pagination information
     * @return page of matching companies
     */
    Page<CompanyDto> find(CompanySearchRequest request, Pageable pageable);

}
