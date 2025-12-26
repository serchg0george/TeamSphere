package com.teamsphere.mapper;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between PositionEntity and PositionDto.
 */
@Component
@RequiredArgsConstructor
public class PositionMapper implements BaseMapper<PositionEntity, PositionDto> {
    /**
     * Converts a PositionEntity to a PositionDto.
     *
     * @param entity the position entity to convert
     * @return the converted position DTO
     */
    @Override
    public PositionDto toDto(PositionEntity entity) {
        return PositionDto.builder()
                .id(entity.getId())
                .positionName(entity.getPositionName())
                .yearsOfExperience(entity.getYearsOfExperience())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts a PositionDto to a PositionEntity.
     *
     * @param dto the position DTO to convert
     * @return the converted position entity
     */
    @Override
    public PositionEntity toEntity(PositionDto dto) {
        return PositionEntity.builder()
                .positionName(dto.getPositionName())
                .yearsOfExperience(dto.getYearsOfExperience())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates a PositionEntity from a PositionDto.
     *
     * @param dto the position DTO containing updated data
     * @param entity the position entity to update
     */
    @Override
    public void updateFromDto(PositionDto dto, PositionEntity entity) {
        entity.setPositionName(dto.getPositionName());
        entity.setYearsOfExperience(dto.getYearsOfExperience());
    }
}
