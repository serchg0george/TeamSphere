package com.teamsphere.service;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService extends GenericService<CompanyEntity, CompanyDto> {

    Page<CompanyDto> find(CompanySearchRequest request, Pageable pageable);

}
