package com.teamsphere.mapper;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper extends BaseMapper<PositionEntity, PositionDto> {
    @Override
    PositionDto toDto(PositionEntity entity);

    @Override
    PositionEntity toEntity(PositionDto dto);
}
