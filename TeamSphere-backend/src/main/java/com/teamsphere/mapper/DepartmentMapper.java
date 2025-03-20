package com.teamsphere.mapper;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentMapper implements BaseMapper<DepartmentEntity, DepartmentDto> {
    @Override
    public DepartmentDto toDto(DepartmentEntity entity) {
        return DepartmentDto.builder()
                .id(entity.getId())
                .departmentName(entity.getDepartmentName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public DepartmentEntity toEntity(DepartmentDto dto) {
        return DepartmentEntity.builder()
                .departmentName(dto.getDepartmentName())
                .description(dto.getDescription())
                .build();
    }

    @Override
    public void updateFromDto(DepartmentDto dto, DepartmentEntity entity) {
        entity.setDepartmentName(dto.getDepartmentName());
        entity.setDescription(dto.getDescription());
    }
}
