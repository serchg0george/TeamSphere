package com.teamsphere.service.impl;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of CompanyService.
 * Provides company management operations including search functionality.
 */
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

    /**
     * Searches for companies using criteria query.
     * Searches across name, industry, address, and email fields.
     *
     * @param request the search criteria
     * @param pageable pagination information
     * @return page of matching companies
     */
    @Override
    public Page<CompanyDto> find(final CompanySearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CompanyEntity> criteriaQuery = criteriaBuilder.createQuery(CompanyEntity.class);
        Root<CompanyEntity> root = criteriaQuery.from(CompanyEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root);
        criteriaQuery.where(mainPredicate);

        TypedQuery<CompanyEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<CompanyEntity> resultList = tQuery.getResultList();
        List<CompanyDto> dtoList = resultList.stream()
                .map(companyMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<CompanyEntity> countRoot = countQuery.from(CompanyEntity.class);
        Predicate countPredicate = buildPredicates(criteriaBuilder, query, countRoot);

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicate);

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id"))
        );

        log.debug("Found {} projects for query '{}'", resultList.size(), request.query());

        return new PageImpl<>(dtoList, sorted, totalCount);
    }

    /**
     * Builds search predicates for company fields.
     *
     * @param criteriaBuilder the criteria builder
     * @param query the search query
     * @param countRoot the root entity
     * @return combined predicate for all searchable fields
     */
    private Predicate buildPredicates(CriteriaBuilder criteriaBuilder, String query, Root<CompanyEntity> countRoot) {
        Predicate nameCount = criteriaBuilder.like(countRoot.get("name"), query);
        Predicate industryCount = criteriaBuilder.like(countRoot.get("industry"), query);
        Predicate addressCount = criteriaBuilder.like(countRoot.get("address"), query);
        Predicate emailCount = criteriaBuilder.like(countRoot.get("email"), query);
        return criteriaBuilder.or(nameCount, industryCount, addressCount, emailCount);
    }

}
