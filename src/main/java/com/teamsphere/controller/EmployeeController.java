package com.teamsphere.controller;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for employee management operations.
 * Provides endpoints for CRUD operations and search functionality for employees.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/employee")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Searches for employees based on search criteria with pagination.
     *
     * @param findEmployee the search criteria for employees
     * @param pageable     pagination information
     * @return ResponseEntity containing a page of matching employees
     */
    @PostMapping("/search")
    public ResponseEntity<Page<EmployeeDto>> searchEmployeeByCriteria(@RequestBody EmployeeSearchRequest findEmployee,
                                                                      Pageable pageable) {
        return ResponseEntity.ok(employeeService.find(findEmployee, pageable));
    }

    /**
     * Creates a new employee.
     *
     * @param employee the employee data to create
     * @return ResponseEntity containing the created employee with location header
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employee) {
        EmployeeDto created = employeeService.save(employee);
        URI location = URI.create("/api/v1/employee/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param employeeId the ID of the employee to retrieve
     * @return ResponseEntity containing the employee data
     */
    @GetMapping("{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {
        return ResponseEntity.ok(employeeService.get(employeeId));
    }

    /**
     * Retrieves all employees with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all employees
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(Pageable pageable) {
        return ResponseEntity.ok(employeeService.getAll(pageable));
    }

    /**
     * Updates an existing employee.
     *
     * @param employeeId the ID of the employee to update
     * @param employee   the updated employee data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updateEmployee(@PathVariable("id") Long employeeId,
                                               @Valid @RequestBody EmployeeDto employee) {
        employeeService.update(employee, employeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param employeeId the ID of the employee to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId) {
        try {
            employeeService.delete(employeeId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}