package com.teamsphere.controller;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for project management operations.
 * Provides endpoints for CRUD operations and search functionality for projects.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/project")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Searches for projects based on search criteria with pagination.
     *
     * @param findProject the search criteria for projects
     * @param pageable    pagination information
     * @return ResponseEntity containing a page of matching projects
     */
    @PostMapping("/search")
    public ResponseEntity<Page<ProjectDto>> searchProject(@RequestBody ProjectSearchRequest findProject,
                                                          Pageable pageable) {
        return ResponseEntity.ok(projectService.find(findProject, pageable));
    }

    /**
     * Creates a new project.
     *
     * @param project the project data to create
     * @return ResponseEntity containing the created project with location header
     */
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto project) {
        ProjectDto created = projectService.save(project);
        URI location = URI.create("/api/v1/project/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId the ID of the project to retrieve
     * @return ResponseEntity containing the project data
     */
    @GetMapping("{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable("id") Long projectId) {
        return ResponseEntity.ok(projectService.get(projectId));
    }

    /**
     * Retrieves all projects with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all projects
     */
    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.getAll(pageable));
    }

    /**
     * Updates an existing project.
     *
     * @param projectId the ID of the project to update
     * @param project   the updated project data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updateProject(@PathVariable("id") Long projectId,
                                              @Valid @RequestBody ProjectDto project) {
        projectService.update(project, projectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long projectId) {
        try {
            projectService.delete(projectId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
