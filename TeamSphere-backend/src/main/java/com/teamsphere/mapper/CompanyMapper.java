package com.teamsphere.mapper;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.mapper.base.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper implements BaseMapper<CompanyEntity, CompanyDto> {

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

    @Override
    public CompanyEntity toEntity(CompanyDto dto) {
        return CompanyEntity.builder()
                .name(dto.getName())
                .industry(dto.getIndustry())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .build();
    }

    @Override
    public void updateFromDto(CompanyDto dto, CompanyEntity entity) {
        entity.setName(dto.getName());
        entity.setIndustry(dto.getIndustry());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
    }
}
