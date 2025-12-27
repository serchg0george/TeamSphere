package com.teamsphere.mapper;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between TaskEntity and TaskDto.
 */
@Component
@RequiredArgsConstructor
public class TaskMapper implements BaseMapper<TaskEntity, TaskDto> {

    /**
     * Converts a TaskEntity to a TaskDto.
     *
     * @param entity the task entity to convert
     * @return the converted task DTO
     */
    @Override
    public TaskDto toDto(TaskEntity entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .taskStatus(entity.getTaskStatus().toString())
                .taskPriority(entity.getTaskPriority().toString())
                .taskType(entity.getTaskType().toString())
                .timeSpentMinutes(entity.getTimeSpentMinutes())
                .taskDescription(entity.getTaskDescription())
                .taskNumber(entity.getTaskNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts a TaskDto to a TaskEntity.
     *
     * @param dto the task DTO to convert
     * @return the converted task entity
     */
    @Override
    public TaskEntity toEntity(TaskDto dto) {

        return TaskEntity.builder()
                .taskStatus(TaskStatus.valueOf(dto.getTaskStatus()))
                .taskPriority(TaskPriority.valueOf(dto.getTaskPriority()))
                .taskType(TaskType.valueOf(dto.getTaskType()))
                .timeSpentMinutes(dto.getTimeSpentMinutes())
                .taskDescription(dto.getTaskDescription())
                .taskNumber(dto.getTaskNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates a TaskEntity from a TaskDto.
     *
     * @param dto    the task DTO containing updated data
     * @param entity the task entity to update
     */
    @Override
    public void updateFromDto(TaskDto dto, TaskEntity entity) {
        entity.setTaskStatus(TaskStatus.valueOf(dto.getTaskStatus()));
        entity.setTaskPriority(TaskPriority.valueOf(dto.getTaskPriority()));
        entity.setTaskType(TaskType.valueOf(dto.getTaskType()));
        entity.setTimeSpentMinutes(dto.getTimeSpentMinutes());
        entity.setTaskDescription(dto.getTaskDescription());
        entity.setTaskNumber(dto.getTaskNumber());
    }
}
