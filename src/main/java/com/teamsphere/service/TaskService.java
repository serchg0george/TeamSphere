package com.teamsphere.service;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService extends GenericService<TaskEntity, TaskDto> {

    Page<TaskDto> find(TaskSearchRequest request, Pageable pageable);

}
