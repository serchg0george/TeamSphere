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
import java.time.LocalDateTime;

/**
 * Mapper for converting between ProjectEntity and ProjectDto.
 */
@Component
@RequiredArgsConstructor
public class ProjectMapper implements BaseMapper<ProjectEntity, ProjectDto> {

    private final CompanyRepository companyRepository;

    /**
     * Converts a ProjectEntity to a ProjectDto.
     *
     * @param entity the project entity to convert
     * @return the converted project DTO
     */
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

    /**
     * Converts a ProjectDto to a ProjectEntity.
     *
     * @param dto the project DTO to convert
     * @return the converted project entity
     */
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates a ProjectEntity from a ProjectDto.
     *
     * @param dto the project DTO containing updated data
     * @param entity the project entity to update
     */
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

    /**
     * Finds a company entity by ID from the project DTO.
     *
     * @param dto the project DTO containing company ID
     * @return the company entity
     * @throws NotFoundException if company is not found
     */
    private CompanyEntity findCompanyById(ProjectDto dto) {
        return companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new NotFoundException(dto.getCompanyId()));
    }
}
