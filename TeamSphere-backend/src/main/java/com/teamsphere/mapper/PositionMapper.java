package com.teamsphere.mapper;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionMapper implements BaseMapper<PositionEntity, PositionDto> {
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

    @Override
    public PositionEntity toEntity(PositionDto dto) {
        return PositionEntity.builder()
                .positionName(dto.getPositionName())
                .yearsOfExperience(dto.getYearsOfExperience())
                .build();
    }

    @Override
    public void updateFromDto(PositionDto dto, PositionEntity entity) {
        entity.setPositionName(dto.getPositionName());
        entity.setYearsOfExperience(dto.getYearsOfExperience());
    }
}
