package com.teamsphere.service;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for project operations.
 * Extends GenericService with project-specific functionality.
 */
public interface ProjectService extends GenericService<ProjectDto> {

    /**
     * Searches for projects based on search criteria.
     *
     * @param request the search criteria
     * @param pageable pagination information
     * @return page of matching projects
     */
    Page<ProjectDto> find(ProjectSearchRequest request, Pageable pageable);

}
