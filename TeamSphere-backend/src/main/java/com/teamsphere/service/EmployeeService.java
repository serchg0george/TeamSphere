package com.teamsphere.service;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService extends GenericService<EmployeeEntity, EmployeeDto> {

    Page<EmployeeDto> findEmployee(EmployeeSearchRequest request, Pageable pageable);

}
