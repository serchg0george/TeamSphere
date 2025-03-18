package com.teamsphere.mapper;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper implements BaseMapper<TaskEntity, TaskDto> {


    @Override
    public TaskDto toDto(TaskEntity entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .timeSpentMinutes(entity.getTimeSpentMinutes())
                .taskDescription(entity.getTaskDescription())
                .taskNumber(entity.getTaskNumber())
                .build();
    }

    @Override
    public TaskEntity toEntity(TaskDto dto) {

        return TaskEntity.builder()
                .timeSpentMinutes(dto.getTimeSpentMinutes())
                .taskDescription(dto.getTaskDescription())
                .taskNumber(dto.getTaskNumber())
                .build();
    }

    @Override
    public void updateFromDto(TaskDto dto, TaskEntity entity) {
        entity.setTimeSpentMinutes(dto.getTimeSpentMinutes());
        entity.setTaskDescription(dto.getTaskDescription());
        entity.setTaskNumber(dto.getTaskNumber());
    }
}
