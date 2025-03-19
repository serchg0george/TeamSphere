package com.teamsphere.service.impl;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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
    public Page<DepartmentDto> find(final DepartmentSearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepartmentEntity> criteriaQuery = criteriaBuilder.createQuery(DepartmentEntity.class);
        Root<DepartmentEntity> root = criteriaQuery.from(DepartmentEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root);
        criteriaQuery.where(mainPredicate);

        TypedQuery<DepartmentEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<DepartmentEntity> resultList = tQuery.getResultList();
        List<DepartmentDto> dtoList = resultList.stream()
                .map(departmentMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DepartmentEntity> countRoot = countQuery.from(DepartmentEntity.class);
        Predicate countPredicate = buildPredicates(criteriaBuilder, query, countRoot);

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicate);

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id"))
        );

        log.debug("Found {} departments for query '{}'", resultList.size(), request.query());

        return new PageImpl<>(dtoList, sorted, totalCount);
    }

    private Predicate buildPredicates(CriteriaBuilder criteriaBuilder, String query, Root<DepartmentEntity> root) {
        Predicate name = criteriaBuilder.like(root.get("departmentName"), query);
        Predicate description = criteriaBuilder.like(root.get("description"), query);
        return criteriaBuilder.or(name, description);
    }
}
