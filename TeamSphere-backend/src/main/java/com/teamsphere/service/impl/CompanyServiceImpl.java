package com.teamsphere.service.impl;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.dto.company.CompanySearchResponse;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.mapper.CompanyMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.CompanyRepository;
import com.teamsphere.service.CompanyService;
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
public class CompanyServiceImpl extends GenericServiceImpl<CompanyEntity, CompanyDto> implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<CompanyEntity, CompanyDto> getMapper() {
        return companyMapper;
    }

    @Override
    public JpaRepository<CompanyEntity, Long> getRepository() {
        return companyRepository;
    }

    @Override
    public CompanySearchResponse findCompany(final CompanySearchRequest request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CompanyEntity> criteriaQuery = criteriaBuilder.createQuery(CompanyEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<CompanyEntity> root = criteriaQuery.from(CompanyEntity.class);

        String query = "%" + request.query() + "%";
        Predicate name = criteriaBuilder.like(root.get("name"), query);
        Predicate industry = criteriaBuilder.like(root.get("industry"), query);
        Predicate address = criteriaBuilder.like(root.get("address"), query);
        Predicate email = criteriaBuilder.like(root.get("email"), query);

        predicates.add(criteriaBuilder.or(name, industry, address, email));

        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<CompanyEntity> tQuery = entityManager.createQuery(criteriaQuery);

        CompanySearchResponse response = new CompanySearchResponse();

        var companies = tQuery.getResultList().stream().map(companyMapper::toDto).toList();

        response.setCompanies(companies);
        response.setCompanyCount(companies.size());

        log.debug("Found {} projects for query '{}'", response.getCompanyCount(), request.query());

        return response;
    }
}
