package com.teamsphere.service.impl;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.mapper.PositionMapper;
import com.teamsphere.repository.PositionRepository;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    private PositionMapper positionMapper;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<PositionEntity> criteriaQuery;

    @Mock
    private Root<PositionEntity> root;

    @Mock
    private Root<PositionEntity> countRoot;

    @Mock
    private Predicate namePredicate;

    @Mock
    private Predicate experiencePredicate;

    @Mock
    private Predicate orPredicate;

    @Mock
    private TypedQuery<PositionEntity> typedQuery;

    @Mock
    private CriteriaQuery<Long> countQuery;

    @Mock
    private TypedQuery<Long> countTypedQuery;

    @InjectMocks
    private PositionServiceImpl positionService;

    private PositionEntity position;
    private PositionDto positionDto;

    @BeforeEach
    void setUp() {
        position = new PositionEntity();
        position.setId(1L);
        position.setPositionName("Developer");
        position.setYearsOfExperience(5);

        positionDto = PositionDto.builder()
                .id(1L)
                .positionName("Developer")
                .yearsOfExperience(5)
                .build();
    }

    private void setupCriteriaQueryMocks() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(PositionEntity.class)).thenReturn(criteriaQuery);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(criteriaQuery.from(PositionEntity.class)).thenReturn(root);
        when(countQuery.from(PositionEntity.class)).thenReturn(countRoot);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
    }

    @Test
    void find_shouldReturnPagedPositions_whenQueryMatchesName() {
        // Given
        setupCriteriaQueryMocks();
        String query = "dev";
        PositionSearchRequest request = new PositionSearchRequest(query);
        Pageable pageable = PageRequest.of(0, 10);
        List<PositionEntity> positions = Collections.singletonList(position);

        // Mocking predicate for main query
        when(criteriaBuilder.like(root.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(criteriaQuery.where(namePredicate)).thenReturn(criteriaQuery);

        // Mocking predicate for count query
        when(criteriaBuilder.like(countRoot.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(namePredicate)).thenReturn(countQuery);

        when(typedQuery.setFirstResult(0)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(10)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(positions);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);
        when(positionMapper.toDto(any(PositionEntity.class))).thenReturn(positionDto);

        // When
        Page<PositionDto> result = positionService.find(request, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(positionDto.getPositionName(), result.getContent().getFirst().getPositionName());
    }

    @Test
    void find_shouldReturnPagedPositions_whenQueryMatchesExperience() {
        // Given
        setupCriteriaQueryMocks();
        String query = "5";
        PositionSearchRequest request = new PositionSearchRequest(query);
        Pageable pageable = PageRequest.of(0, 10);
        List<PositionEntity> positions = Collections.singletonList(position);

        // Mocking predicate for main query
        when(criteriaBuilder.like(root.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(criteriaBuilder.equal(root.get("yearsOfExperience"), 5)).thenReturn(experiencePredicate);
        when(criteriaBuilder.or(namePredicate, experiencePredicate)).thenReturn(orPredicate);
        when(criteriaQuery.where(orPredicate)).thenReturn(criteriaQuery);

        // Mocking predicate for count query
        when(criteriaBuilder.like(countRoot.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(criteriaBuilder.equal(countRoot.get("yearsOfExperience"), 5)).thenReturn(experiencePredicate);
        when(criteriaBuilder.or(namePredicate, experiencePredicate)).thenReturn(orPredicate);
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(orPredicate)).thenReturn(countQuery);

        when(typedQuery.setFirstResult(0)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(10)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(positions);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);
        when(positionMapper.toDto(any(PositionEntity.class))).thenReturn(positionDto);

        // When
        Page<PositionDto> result = positionService.find(request, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(positionDto.getYearsOfExperience(), result.getContent().getFirst().getYearsOfExperience());
    }

    @Test
    void find_shouldReturnEmptyPage_whenNoPositionsMatch() {
        // Given
        setupCriteriaQueryMocks();
        String query = "nonexistent";
        PositionSearchRequest request = new PositionSearchRequest(query);
        Pageable pageable = PageRequest.of(0, 10);
        List<PositionEntity> emptyList = Collections.emptyList();

        // Mocking predicate for main query
        when(criteriaBuilder.like(root.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(criteriaQuery.where(namePredicate)).thenReturn(criteriaQuery);

        // Mocking predicate for count query
        when(criteriaBuilder.like(countRoot.get("positionName"), "%" + query + "%")).thenReturn(namePredicate);
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(namePredicate)).thenReturn(countQuery);

        when(typedQuery.setFirstResult(0)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(10)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(emptyList);
        when(countTypedQuery.getSingleResult()).thenReturn(0L);

        // When
        Page<PositionDto> result = positionService.find(request, pageable);

        // Then
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("getMapper should return PositionMapper")
    void testGetMapper() {
        com.teamsphere.mapper.base.BaseMapper<PositionEntity, PositionDto> mapper = positionService.getMapper();
        assertNotNull(mapper);
        assertEquals(positionMapper, mapper);
    }

    @Test
    @DisplayName("getRepository should return PositionRepository")
    void testGetRepository() {
        org.springframework.data.jpa.repository.JpaRepository<PositionEntity, Long> repository = positionService.getRepository();
        assertNotNull(repository);
        assertEquals(positionRepository, repository);
    }
}