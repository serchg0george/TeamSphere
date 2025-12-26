package com.teamsphere.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.entity.DepartmentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class DepartmentMapperTest {

    @InjectMocks
    private DepartmentMapper departmentMapper;

    @Test
    void toDto_shouldMapEntityToDto() {
        // Given
        DepartmentEntity entity = new DepartmentEntity();
        entity.setId(1L);
        entity.setDepartmentName("Engineering");
        entity.setDescription("Engineering Department");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When
        DepartmentDto dto = departmentMapper.toDto(entity);

        // Then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getDepartmentName(), dto.getDepartmentName());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        DepartmentDto dto = DepartmentDto.builder()
                .departmentName("Human Resources")
                .description("HR Department")
                .build();

        // When
        DepartmentEntity entity = departmentMapper.toEntity(dto);

        // Then
        assertEquals(dto.getDepartmentName(), entity.getDepartmentName());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        DepartmentDto dto = DepartmentDto.builder()
                .departmentName("Updated Engineering")
                .description("Updated Engineering Department")
                .build();

        DepartmentEntity entity = new DepartmentEntity();
        entity.setDepartmentName("Engineering");
        entity.setDescription("Engineering Department");

        // When
        departmentMapper.updateFromDto(dto, entity);

        // Then
        assertEquals(dto.getDepartmentName(), entity.getDepartmentName());
        assertEquals(dto.getDescription(), entity.getDescription());
    }
}