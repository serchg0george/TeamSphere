package com.teamsphere.mapper;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ProjectMapper implements BaseMapper<ProjectEntity, ProjectDto> {

    private final CompanyRepository companyRepository;

    @Override
    public ProjectDto toDto(ProjectEntity entity) {

        LocalDate finishDateEntity = entity.getFinishDate();
        String finishDate = null;

        if (finishDateEntity != null) {
            finishDate = finishDateEntity.toString();
        }

        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .startDate(entity.getStartDate().toString())
                .finishDate(finishDate)
                .status(entity.getStatus().toString())
                .companyId(entity.getCompany().getId())
                .companyName(entity.getCompany().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public ProjectEntity toEntity(ProjectDto dto) {
        CompanyEntity company = findCompanyById(dto);

        LocalDate finishDate = (dto.getFinishDate() != null) ? LocalDate.parse(dto.getFinishDate()) : null;

        return ProjectEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(LocalDate.parse(dto.getStartDate()))
                .finishDate(finishDate)
                .status(ProjectStatus.valueOf(dto.getStatus()))
                .company(company)
                .build();
    }

    @Override
    public void updateFromDto(ProjectDto dto, ProjectEntity entity) {
        CompanyEntity company = findCompanyById(dto);

        LocalDate finishDate = (dto.getFinishDate() != null) ? LocalDate.parse(dto.getFinishDate()) : null;

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setFinishDate(finishDate);
        entity.setStatus(ProjectStatus.valueOf(dto.getStatus()));
        entity.setCompany(company);
    }

    private CompanyEntity findCompanyById(ProjectDto dto) {
        return companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new NotFoundException(dto.getCompanyId()));
    }
}
