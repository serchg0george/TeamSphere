package com.teamsphere.mapper;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper extends BaseMapper<DepartmentEntity, DepartmentDto> {
    @Override
    DepartmentDto toDto(DepartmentEntity entity);

    @Override
    DepartmentEntity toEntity(DepartmentDto dto);
}
