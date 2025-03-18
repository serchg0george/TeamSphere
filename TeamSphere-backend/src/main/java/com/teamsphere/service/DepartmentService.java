package com.teamsphere.service;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService extends GenericService<DepartmentEntity, DepartmentDto> {

    Page<DepartmentDto> findDepartment(DepartmentSearchRequest request, Pageable pageable);

}
