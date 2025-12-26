package com.teamsphere.service.impl;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.mapper.DepartmentMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.DepartmentRepository;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private DepartmentDto departmentDto;
    private DepartmentEntity departmentEntity;

    @BeforeEach
    void setUp() {
        departmentDto = DepartmentDto.builder()
                .id(1L)
                .departmentName("Test Department")
                .description("Test Description")
                .build();

        departmentEntity = new DepartmentEntity();
        departmentEntity.setId(1L);
        departmentEntity.setDepartmentName("Test Department");
        departmentEntity.setDescription("Test Description");
    }

    @Test
    void testFind() {
        DepartmentSearchRequest request = new DepartmentSearchRequest("Test");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<DepartmentEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<DepartmentEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<DepartmentEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(DepartmentEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(DepartmentEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(departmentEntity));
        when(departmentMapper.toDto(any(DepartmentEntity.class))).thenReturn(departmentDto);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);

        Page<DepartmentDto> result = departmentService.find(request, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(departmentDto, result.getContent().getFirst());
    }

    @Test
    @DisplayName("getMapper should return DepartmentMapper")
    void testGetMapper() {
        BaseMapper<DepartmentEntity, DepartmentDto> mapper = departmentService.getMapper();
        assertNotNull(mapper);
        assertEquals(departmentMapper, mapper);
    }

    @Test
    @DisplayName("getRepository should return DepartmentRepository")
    void testGetRepository() {
        JpaRepository<DepartmentEntity, Long> repository = departmentService.getRepository();
        assertNotNull(repository);
        assertEquals(departmentRepository, repository);
    }

    @Test
    @DisplayName("find should return empty page when no departments match")
    void testFind_emptyResult() {
        DepartmentSearchRequest request = new DepartmentSearchRequest("NonExistent");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<DepartmentEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<DepartmentEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<DepartmentEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(DepartmentEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(DepartmentEntity.class)).thenReturn(root);
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

        Page<DepartmentDto> result = departmentService.find(request, pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}