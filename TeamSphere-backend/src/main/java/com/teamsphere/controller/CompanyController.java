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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/company")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/search")
    public ResponseEntity<Page<CompanyDto>> searchCompany(@RequestBody CompanySearchRequest findCompany,
                                                          Pageable pageable) {
        return ResponseEntity.ok(companyService.find(findCompany, pageable));
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyDto company) {
        CompanyDto created = companyService.save(company);
        URI location = URI.create(String.format("/api/v1/company/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable("id") Long companyId) {
        return ResponseEntity.ok(companyService.get(companyId));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(Pageable pageable) {
        return ResponseEntity.ok(companyService.getAll(pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateCompany(@PathVariable("id") Long companyId,
                                              @Valid @RequestBody CompanyDto company) {
        companyService.update(company, companyId);
        return ResponseEntity.noContent().build();
    }

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
