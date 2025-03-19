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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/department")
@Validated
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/search")
    public ResponseEntity<Page<DepartmentDto>> searchDepartment(@RequestBody DepartmentSearchRequest findDepartment,
                                                                Pageable pageable) {
        return ResponseEntity.ok(departmentService.find(findDepartment, pageable));
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto department) {
        DepartmentDto created = departmentService.save(department);
        URI location = URI.create("/api/v1/department/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") Long departmentId) {
        return ResponseEntity.ok(departmentService.get(departmentId));
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentDto>> getAllDepartments(Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAll(pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateDepartment(@PathVariable("id") Long departmentId,
                                                 @Valid @RequestBody DepartmentDto department) {
        departmentService.update(department, departmentId);
        return ResponseEntity.noContent().build();
    }

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
