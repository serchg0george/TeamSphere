package com.teamsphere.service;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for task operations.
 * Extends GenericService with task-specific functionality.
 */
public interface TaskService extends GenericService<TaskDto> {

    /**
     * Searches for tasks based on search criteria.
     *
     * @param request  the search criteria
     * @param pageable pagination information
     * @return page of matching tasks
     */
    Page<TaskDto> find(TaskSearchRequest request, Pageable pageable);

}
