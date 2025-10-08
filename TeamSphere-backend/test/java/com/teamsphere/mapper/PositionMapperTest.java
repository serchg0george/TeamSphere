package com.teamsphere.mapper;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.entity.PositionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PositionMapperTest {

    @InjectMocks
    private PositionMapper positionMapper;

    @Test
    void toDto_shouldMapEntityToDto() {
        // Given
        PositionEntity entity = new PositionEntity();
        entity.setId(1L);
        entity.setPositionName("Developer");
        entity.setYearsOfExperience(5);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When
        PositionDto dto = positionMapper.toDto(entity);

        // Then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPositionName(), dto.getPositionName());
        assertEquals(entity.getYearsOfExperience(), dto.getYearsOfExperience());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        PositionDto dto = PositionDto.builder()
                .positionName("Manager")
                .yearsOfExperience(10)
                .build();

        // When
        PositionEntity entity = positionMapper.toEntity(dto);

        // Then
        assertEquals(dto.getPositionName(), entity.getPositionName());
        assertEquals(dto.getYearsOfExperience(), entity.getYearsOfExperience());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        PositionDto dto = PositionDto.builder()
                .positionName("Senior Developer")
                .yearsOfExperience(8)
                .build();

        PositionEntity entity = new PositionEntity();
        entity.setPositionName("Developer");
        entity.setYearsOfExperience(5);

        // When
        positionMapper.updateFromDto(dto, entity);

        // Then
        assertEquals(dto.getPositionName(), entity.getPositionName());
        assertEquals(dto.getYearsOfExperience(), entity.getYearsOfExperience());
    }
}