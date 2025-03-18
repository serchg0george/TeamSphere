package com.teamsphere.service.impl;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.dto.employee.EmployeeSearchResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public EmployeeSearchResponse findEmployee(final EmployeeSearchRequest request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeEntity> criteriaQuery = criteriaBuilder.createQuery(EmployeeEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<EmployeeEntity> root = criteriaQuery.from(EmployeeEntity.class);


        String query = "%" + request.query() + "%";
        Predicate firstName = criteriaBuilder.like(root.get("firstName"), query);
        Predicate lastName = criteriaBuilder.like(root.get("lastName"), query);
        Predicate email = criteriaBuilder.like(root.get("email"), query);

        try {
            Integer pinQuery = Integer.parseInt(request.query());
            Predicate pin = criteriaBuilder.equal(root.get("pin"), pinQuery);
            predicates.add(criteriaBuilder.or(firstName, lastName, email, pin));
        } catch (NumberFormatException e) {
            log.info("Query '{}' is not a valid pin", e.getMessage());
        }

        predicates.add(criteriaBuilder.or(firstName, lastName, email));


        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<EmployeeEntity> tQuery = entityManager.createQuery(criteriaQuery);

        EmployeeSearchResponse response = new EmployeeSearchResponse();

        var employees = tQuery.getResultList().stream().map(employeeMapper::toDto).toList();

        response.setEmployees(employees);
        response.setEmployeeCount(employees.size());

        log.debug("Found {} projects for query '{}'", response.getEmployeeCount(), request.query());

        return response;
    }
}
