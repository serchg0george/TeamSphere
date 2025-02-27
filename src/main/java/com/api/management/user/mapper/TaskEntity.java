package com.api.management.user.mapper;

import com.api.management.user.dto.task.TaskDto;
import com.api.management.user.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskEntity extends BaseMapper<TaskEntity, TaskDto> {
    @Override
    TaskDto mapEntityToDto(TaskEntity entity);

    @Override
    TaskEntity mapDtoToEntity(TaskDto dto);
}
