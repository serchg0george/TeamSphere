package com.teamsphere.controller;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for company management operations.
 * Provides endpoints for CRUD operations and search functionality for companies.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/company")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Searches for companies based on search criteria with pagination.
     *
     * @param findCompany the search criteria for companies
     * @param pageable    pagination information
     * @return ResponseEntity containing a page of matching companies
     */
    @PostMapping("/search")
    public ResponseEntity<Page<CompanyDto>> searchCompany(@RequestBody CompanySearchRequest findCompany,
                                                          Pageable pageable) {
        return ResponseEntity.ok(companyService.find(findCompany, pageable));
    }

    /**
     * Creates a new company.
     *
     * @param company the company data to create
     * @return ResponseEntity containing the created company with location header
     */
    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyDto company) {
        CompanyDto created = companyService.save(company);
        URI location = URI.create(String.format("/api/v1/company/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a company by its ID.
     *
     * @param companyId the ID of the company to retrieve
     * @return ResponseEntity containing the company data
     */
    @GetMapping("{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable("id") Long companyId) {
        return ResponseEntity.ok(companyService.get(companyId));
    }

    /**
     * Retrieves all companies with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all companies
     */
    @GetMapping
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(Pageable pageable) {
        return ResponseEntity.ok(companyService.getAll(pageable));
    }

    /**
     * Updates an existing company.
     *
     * @param companyId the ID of the company to update
     * @param company   the updated company data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updateCompany(@PathVariable("id") Long companyId,
                                              @Valid @RequestBody CompanyDto company) {
        companyService.update(company, companyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a company by its ID.
     *
     * @param companyId the ID of the company to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") Long companyId) {
        try {
            companyService.delete(companyId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
