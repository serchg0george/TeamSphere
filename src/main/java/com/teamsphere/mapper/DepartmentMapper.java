package com.teamsphere.mapper;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between DepartmentEntity and DepartmentDto.
 */
@Component
@RequiredArgsConstructor
public class DepartmentMapper implements BaseMapper<DepartmentEntity, DepartmentDto> {
    /**
     * Converts a DepartmentEntity to a DepartmentDto.
     *
     * @param entity the department entity to convert
     * @return the converted department DTO
     */
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

    /**
     * Converts a DepartmentDto to a DepartmentEntity.
     *
     * @param dto the department DTO to convert
     * @return the converted department entity
     */
    @Override
    public DepartmentEntity toEntity(DepartmentDto dto) {
        return DepartmentEntity.builder()
                .departmentName(dto.getDepartmentName())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates a DepartmentEntity from a DepartmentDto.
     *
     * @param dto the department DTO containing updated data
     * @param entity the department entity to update
     */
    @Override
    public void updateFromDto(DepartmentDto dto, DepartmentEntity entity) {
        entity.setDepartmentName(dto.getDepartmentName());
        entity.setDescription(dto.getDescription());
    }
}
