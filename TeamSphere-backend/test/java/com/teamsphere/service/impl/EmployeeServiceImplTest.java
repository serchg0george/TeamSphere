package com.teamsphere.service.impl;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.entity.EmployeeEntity;
import com.teamsphere.mapper.EmployeeMapper;
import com.teamsphere.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDto employeeDto;
    private EmployeeEntity employeeEntity;

    @BeforeEach
    void setUp() {
        employeeDto = EmployeeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        employeeEntity = new EmployeeEntity();
        employeeEntity.setId(1L);
        employeeEntity.setFirstName("John");
        employeeEntity.setLastName("Doe");
        employeeEntity.setEmail("john.doe@example.com");
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findAllWithRelations()).thenReturn(Collections.singletonList(employeeEntity));
        when(employeeMapper.toDto(any(EmployeeEntity.class))).thenReturn(employeeDto);

        Page<EmployeeDto> result = employeeService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(employeeDto, result.getContent().get(0));
    }

    @Test
    void testFind() {
        EmployeeSearchRequest request = new EmployeeSearchRequest("John");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<EmployeeEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<EmployeeEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<EmployeeEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(EmployeeEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(EmployeeEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(employeeEntity));
        when(employeeMapper.toDto(any(EmployeeEntity.class))).thenReturn(employeeDto);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);

        Page<EmployeeDto> result = employeeService.find(request, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(employeeDto, result.getContent().get(0));
    }
}