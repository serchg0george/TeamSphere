package com.teamsphere.mapper;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between CompanyEntity and CompanyDto.
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper implements BaseMapper<CompanyEntity, CompanyDto> {

    /**
     * Converts a CompanyEntity to a CompanyDto.
     *
     * @param entity the company entity to convert
     * @return the converted company DTO
     */
    @Override
    public CompanyDto toDto(CompanyEntity entity) {
        return CompanyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .industry(entity.getIndustry())
                .address(entity.getAddress())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts a CompanyDto to a CompanyEntity.
     *
     * @param dto the company DTO to convert
     * @return the converted company entity
     */
    @Override
    public CompanyEntity toEntity(CompanyDto dto) {
        return CompanyEntity.builder()
                .name(dto.getName())
                .industry(dto.getIndustry())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates a CompanyEntity from a CompanyDto.
     *
     * @param dto    the company DTO containing updated data
     * @param entity the company entity to update
     */
    @Override
    public void updateFromDto(CompanyDto dto, CompanyEntity entity) {
        entity.setName(dto.getName());
        entity.setIndustry(dto.getIndustry());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
    }
}
