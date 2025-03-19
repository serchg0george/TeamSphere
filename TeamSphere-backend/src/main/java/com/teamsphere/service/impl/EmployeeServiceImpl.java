package com.teamsphere.service.impl;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.entity.EmployeeEntity;
import com.teamsphere.mapper.EmployeeMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.EmployeeRepository;
import com.teamsphere.service.EmployeeService;
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
public class EmployeeServiceImpl extends GenericServiceImpl<EmployeeEntity, EmployeeDto> implements EmployeeService {

    private final EmployeeRepository peopleRepository;
    private final EmployeeMapper employeeMapper;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<EmployeeEntity, EmployeeDto> getMapper() {
        return employeeMapper;
    }

    @Override
    public JpaRepository<EmployeeEntity, Long> getRepository() {
        return peopleRepository;
    }

    @Override
    public Page<EmployeeDto> getAll(Pageable page) {
        List<EmployeeEntity> employees = peopleRepository.findAllWithRelations();
        return new PageImpl<>(employees.stream().map(employeeMapper::toDto).toList(), page, employees.size());
    }

    @Override
    public Page<EmployeeDto> find(final EmployeeSearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeEntity> criteriaQuery = criteriaBuilder.createQuery(EmployeeEntity.class);
        Root<EmployeeEntity> root = criteriaQuery.from(EmployeeEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root);
        criteriaQuery.where(mainPredicate);

        TypedQuery<EmployeeEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<EmployeeEntity> resultList = tQuery.getResultList();
        List<EmployeeDto> dtoList = resultList.stream()
                .map(employeeMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<EmployeeEntity> countRoot = countQuery.from(EmployeeEntity.class);
        Predicate countPredicate = buildPredicates(criteriaBuilder, query, countRoot);

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicate);

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id")));

        log.debug("Found {} employees for query '{}'", resultList.size(), request.query());

        return new PageImpl<>(dtoList, sorted, totalCount);
    }

    private Predicate buildPredicates(final CriteriaBuilder criteriaBuilder, final String query, final Root<EmployeeEntity> root) {
        Predicate firstName = criteriaBuilder.like(root.get("firstName"), query);
        Predicate lastName = criteriaBuilder.like(root.get("lastName"), query);
        Predicate email = criteriaBuilder.like(root.get("email"), query);
        try {
            Integer pinQuery = Integer.parseInt(query);
            Predicate pin = criteriaBuilder.equal(root.get("pin"), pinQuery);
            return criteriaBuilder.or(firstName, lastName, email, pin);
        } catch (NumberFormatException e) {
            log.info("Query '{}' is not a valid pin", e.getMessage());
        }

        return criteriaBuilder.or(firstName, lastName, email);
    }
}
