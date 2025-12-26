package com.teamsphere.service.impl;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.mapper.ProjectMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectDto projectDto;
    private ProjectEntity projectEntity;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.IN_PROGRESS.toString())
                .startDate(LocalDate.now().toString())
                .build();

        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);
        projectEntity.setName("Test Project");
        projectEntity.setDescription("Test Description");
        projectEntity.setStatus(ProjectStatus.IN_PROGRESS);
        projectEntity.setStartDate(LocalDate.now());
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.findAllWithCompanies()).thenReturn(Collections.singletonList(projectEntity));
        when(projectMapper.toDto(any(ProjectEntity.class))).thenReturn(projectDto);

        Page<ProjectDto> result = projectService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(projectDto, result.getContent().getFirst());
    }

    @Test
    void testFind() {
        ProjectSearchRequest request = new ProjectSearchRequest("Test");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<ProjectEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<ProjectEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<ProjectEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(ProjectEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(ProjectEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(projectEntity));
        when(projectMapper.toDto(any(ProjectEntity.class))).thenReturn(projectDto);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);

        Page<ProjectDto> result = projectService.find(request, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(projectDto, result.getContent().getFirst());
    }

    @Test
    @DisplayName("getMapper should return ProjectMapper")
    void testGetMapper() {
        BaseMapper<ProjectEntity, ProjectDto> mapper = projectService.getMapper();
        assertNotNull(mapper);
        assertEquals(projectMapper, mapper);
    }

    @Test
    @DisplayName("getRepository should return ProjectRepository")
    void testGetRepository() {
        JpaRepository<ProjectEntity, Long> repository = projectService.getRepository();
        assertNotNull(repository);
        assertEquals(projectRepository, repository);
    }

    @Test
    @DisplayName("getAll should return empty page when no projects exist")
    void testGetAll_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.findAllWithCompanies()).thenReturn(Collections.emptyList());

        Page<ProjectDto> result = projectService.getAll(pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("find should handle invalid date format query")
    void testFind_withInvalidDateQuery() {
        ProjectSearchRequest request = new ProjectSearchRequest("invalid-date");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<ProjectEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<ProjectEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<ProjectEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(ProjectEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(ProjectEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(0L);

        Page<ProjectDto> result = projectService.find(request, pageable);

        assertEquals(0, result.getTotalElements());
        // The search should not throw exception and should return results based on name/description predicates
        verify(criteriaBuilder, times(2)).or(any(Predicate.class), any(Predicate.class));
    }
}