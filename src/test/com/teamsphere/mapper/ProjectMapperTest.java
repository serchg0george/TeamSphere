package com.teamsphere.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ProjectMapper projectMapper;

    private ProjectEntity projectEntity;
    private CompanyEntity companyEntity;

    @BeforeEach
    void setUp() {
        companyEntity = CompanyEntity.builder()
                .id(1L)
                .name("Test Company")
                .build();

        projectEntity = ProjectEntity.builder()
                .id(1L)
                .name("Project Phoenix")
                .description("A test project.")
                .startDate(LocalDate.now())
                .finishDate(LocalDate.now().plusMonths(6))
                .status(ProjectStatus.IN_PROGRESS)
                .company(companyEntity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        // When
        ProjectDto dto = projectMapper.toDto(projectEntity);

        // Then
        assertEquals(projectEntity.getId(), dto.getId());
        assertEquals(projectEntity.getName(), dto.getName());
        assertEquals(projectEntity.getDescription(), dto.getDescription());
        assertEquals(projectEntity.getStartDate().toString(), dto.getStartDate());
        assertEquals(projectEntity.getFinishDate().toString(), dto.getFinishDate());
        assertEquals(projectEntity.getStatus().toString(), dto.getStatus());
        assertEquals(companyEntity.getId(), dto.getCompanyId());
        assertEquals(companyEntity.getName(), dto.getCompanyName());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        ProjectDto dto = ProjectDto.builder()
                .name("Project Titan")
                .description("Another test project.")
                .startDate(LocalDate.now().toString())
                .status(ProjectStatus.IN_PROGRESS.toString())
                .companyId(1L)
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyEntity));

        // When
        ProjectEntity entity = projectMapper.toEntity(dto);

        // Then
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getStartDate(), entity.getStartDate().toString());
        assertEquals(dto.getStatus(), entity.getStatus().toString());
        assertNotNull(entity.getCompany());
        assertEquals(companyEntity.getId(), entity.getCompany().getId());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        ProjectDto dto = ProjectDto.builder()
                .name("Updated Project Phoenix")
                .description("An updated test project.")
                .startDate(LocalDate.now().plusDays(1).toString())
                .finishDate(LocalDate.now().plusMonths(7).toString())
                .status(ProjectStatus.FINISHED.toString())
                .companyId(1L)
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyEntity));

        // When
        projectMapper.updateFromDto(dto, projectEntity);

        // Then
        assertEquals(dto.getName(), projectEntity.getName());
        assertEquals(dto.getDescription(), projectEntity.getDescription());
        assertEquals(dto.getStartDate(), projectEntity.getStartDate().toString());
        assertEquals(dto.getFinishDate(), projectEntity.getFinishDate().toString());
        assertEquals(dto.getStatus(), projectEntity.getStatus().toString());
        assertEquals(companyEntity.getId(), projectEntity.getCompany().getId());
    }
}