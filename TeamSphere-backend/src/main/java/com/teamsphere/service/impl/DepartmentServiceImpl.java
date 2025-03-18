package com.teamsphere.service.impl;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.dto.department.DepartmentSearchResponse;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.mapper.DepartmentMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.DepartmentRepository;
import com.teamsphere.service.DepartmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DepartmentServiceImpl extends GenericServiceImpl<DepartmentEntity, DepartmentDto> implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DepartmentRepository departmentRepository;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<DepartmentEntity, DepartmentDto> getMapper() {
        return departmentMapper;
    }

    @Override
    public JpaRepository<DepartmentEntity, Long> getRepository() {
        return departmentRepository;
    }

    @Override
    public DepartmentSearchResponse findDepartment(final DepartmentSearchRequest request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepartmentEntity> criteriaQuery = criteriaBuilder.createQuery(DepartmentEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<DepartmentEntity> root = criteriaQuery.from(DepartmentEntity.class);

        String query = "%" + request.query() + "%";
        Predicate departmentName = criteriaBuilder.like(root.get("departmentName"), query);
        Predicate description = criteriaBuilder.like(root.get("description"), query);

        predicates.add(criteriaBuilder.or(departmentName, description));

        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<DepartmentEntity> tQuery = entityManager.createQuery(criteriaQuery);

        DepartmentSearchResponse response = new DepartmentSearchResponse();

        var departments = tQuery.getResultList().stream().map(departmentMapper::toDto).toList();

        response.setDepartments(departments);
        response.setDepartmentCount(departments.size());

        log.debug("Found {} projects for query '{}'", response.getDepartmentCount(), request.query());

        return response;
    }
}
