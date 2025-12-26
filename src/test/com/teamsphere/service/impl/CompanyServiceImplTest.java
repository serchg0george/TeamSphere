package com.teamsphere.service.impl;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.mapper.CompanyMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.CompanyRepository;
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
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private CompanyDto companyDto;
    private CompanyEntity companyEntity;

    @BeforeEach
    void setUp() {
        companyDto = CompanyDto.builder()
                .id(1L)
                .name("Test Company")
                .industry("Test Industry")
                .address("Test Address")
                .email("test@example.com")
                .build();

        companyEntity = new CompanyEntity();
        companyEntity.setId(1L);
        companyEntity.setName("Test Company");
        companyEntity.setIndustry("Test Industry");
        companyEntity.setAddress("Test Address");
        companyEntity.setEmail("test@example.com");
    }

    @Test
    void testFind() {
        CompanySearchRequest request = new CompanySearchRequest("Test");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<CompanyEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<CompanyEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<CompanyEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(CompanyEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(CompanyEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(companyEntity));
        when(companyMapper.toDto(any(CompanyEntity.class))).thenReturn(companyDto);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);

        Page<CompanyDto> result = companyService.find(request, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(companyDto, result.getContent().getFirst());
    }

    @Test
    @DisplayName("getMapper should return CompanyMapper")
    void testGetMapper() {
        BaseMapper<CompanyEntity, CompanyDto> mapper = companyService.getMapper();
        assertNotNull(mapper);
        assertEquals(companyMapper, mapper);
    }

    @Test
    @DisplayName("getRepository should return CompanyRepository")
    void testGetRepository() {
        JpaRepository<CompanyEntity, Long> repository = companyService.getRepository();
        assertNotNull(repository);
        assertEquals(companyRepository, repository);
    }

    @Test
    @DisplayName("find should return empty page when no companies match")
    void testFind_emptyResult() {
        CompanySearchRequest request = new CompanySearchRequest("NonExistent");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<CompanyEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<CompanyEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<CompanyEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(CompanyEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(CompanyEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
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

        Page<CompanyDto> result = companyService.find(request, pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}