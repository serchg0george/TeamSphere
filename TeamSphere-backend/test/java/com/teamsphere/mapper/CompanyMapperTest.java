package com.teamsphere.mapper;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.entity.CompanyEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CompanyMapperTest {

    @InjectMocks
    private CompanyMapper companyMapper;

    @Test
    void toDto_shouldMapEntityToDto() {
        // Given
        CompanyEntity entity = new CompanyEntity();
        entity.setId(1L);
        entity.setName("Tech Corp");
        entity.setIndustry("Technology");
        entity.setAddress("123 Tech Street");
        entity.setEmail("contact@techcorp.com");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When
        CompanyDto dto = companyMapper.toDto(entity);

        // Then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getIndustry(), dto.getIndustry());
        assertEquals(entity.getAddress(), dto.getAddress());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        CompanyDto dto = CompanyDto.builder()
                .name("Innovate LLC")
                .industry("Innovation")
                .address("456 Innovate Avenue")
                .email("info@innovatellc.com")
                .build();

        // When
        CompanyEntity entity = companyMapper.toEntity(dto);

        // Then
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getIndustry(), entity.getIndustry());
        assertEquals(dto.getAddress(), entity.getAddress());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        CompanyDto dto = CompanyDto.builder()
                .name("Updated Tech Corp")
                .industry("Advanced Technology")
                .address("789 Updated Street")
                .email("updated@techcorp.com")
                .build();

        CompanyEntity entity = new CompanyEntity();
        entity.setName("Tech Corp");
        entity.setIndustry("Technology");
        entity.setAddress("123 Tech Street");
        entity.setEmail("contact@techcorp.com");

        // When
        companyMapper.updateFromDto(dto, entity);

        // Then
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getIndustry(), entity.getIndustry());
        assertEquals(dto.getAddress(), entity.getAddress());
        assertEquals(dto.getEmail(), entity.getEmail());
    }
}