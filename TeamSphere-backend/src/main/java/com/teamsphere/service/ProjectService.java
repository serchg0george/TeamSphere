package com.teamsphere.service;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService extends GenericService<ProjectEntity, ProjectDto> {

    Page<ProjectDto> findProject(ProjectSearchRequest request, Pageable pageable);

}
