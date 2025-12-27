package com.teamsphere.controller;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for department management operations.
 * Provides endpoints for CRUD operations and search functionality for departments.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/department")
@Validated
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Searches for departments based on search criteria with pagination.
     *
     * @param findDepartment the search criteria for departments
     * @param pageable       pagination information
     * @return ResponseEntity containing a page of matching departments
     */
    @PostMapping("/search")
    public ResponseEntity<Page<DepartmentDto>> searchDepartment(@RequestBody DepartmentSearchRequest findDepartment,
                                                                Pageable pageable) {
        return ResponseEntity.ok(departmentService.find(findDepartment, pageable));
    }

    /**
     * Creates a new department.
     *
     * @param department the department data to create
     * @return ResponseEntity containing the created department with location header
     */
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto department) {
        DepartmentDto created = departmentService.save(department);
        URI location = URI.create("/api/v1/department/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a department by its ID.
     *
     * @param departmentId the ID of the department to retrieve
     * @return ResponseEntity containing the department data
     */
    @GetMapping("{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") Long departmentId) {
        return ResponseEntity.ok(departmentService.get(departmentId));
    }

    /**
     * Retrieves all departments with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all departments
     */
    @GetMapping
    public ResponseEntity<Page<DepartmentDto>> getAllDepartments(Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAll(pageable));
    }

    /**
     * Updates an existing department.
     *
     * @param departmentId the ID of the department to update
     * @param department   the updated department data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updateDepartment(@PathVariable("id") Long departmentId,
                                                 @Valid @RequestBody DepartmentDto department) {
        departmentService.update(department, departmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a department by its ID.
     *
     * @param departmentId the ID of the department to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable("id") Long departmentId) {
        try {
            departmentService.delete(departmentId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
