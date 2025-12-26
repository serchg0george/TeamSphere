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

@Component
@RequiredArgsConstructor
public class TaskMapper implements BaseMapper<TaskEntity, TaskDto> {


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
