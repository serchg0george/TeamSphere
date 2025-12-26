package com.teamsphere.service.impl;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.mapper.PositionMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.PositionRepository;
import com.teamsphere.service.PositionService;
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
 * Implementation of PositionService.
 * Provides position management operations including search functionality.
 */
@Service
@Slf4j
@AllArgsConstructor
public class PositionServiceImpl extends GenericServiceImpl<PositionEntity, PositionDto> implements PositionService {

    private final PositionMapper positionMapper;
    private final PositionRepository positionRepository;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<PositionEntity, PositionDto> getMapper() {
        return positionMapper;
    }

    @Override
    public JpaRepository<PositionEntity, Long> getRepository() {
        return positionRepository;
    }

    /**
     * Searches for positions using criteria query.
     * Searches across position name and years of experience fields.
     *
     * @param request the search criteria
     * @param pageable pagination information
     * @return page of matching positions
     */
    @Override
    public Page<PositionDto> find(final PositionSearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PositionEntity> criteriaQuery = criteriaBuilder.createQuery(PositionEntity.class);
        Root<PositionEntity> root = criteriaQuery.from(PositionEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root, request.query());
        criteriaQuery.where(mainPredicate);

        TypedQuery<PositionEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<PositionEntity> resultList = tQuery.getResultList();
        List<PositionDto> dtoList = resultList.stream()
                .map(positionMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<PositionEntity> countRoot = countQuery.from(PositionEntity.class);
        Predicate countPredicate = buildPredicates(criteriaBuilder, query, countRoot, request.query());

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicate);

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id")));

        log.debug("Found {} positions for query '{}'", resultList.size(), request.query());

        return new PageImpl<>(dtoList, sorted, totalCount);
    }

    /**
     * Builds search predicates for position fields.
     * Attempts to parse query as years of experience if possible.
     *
     * @param criteriaBuilder the criteria builder
     * @param query the search query with wildcards
     * @param root the root entity
     * @param rawQuery the raw search query without wildcards
     * @return combined predicate for all searchable fields
     */
    private Predicate buildPredicates(final CriteriaBuilder criteriaBuilder, final String query, final Root<PositionEntity> root, final String rawQuery) {
        Predicate roleName = criteriaBuilder.like(root.get("positionName"), query);

        try {
            Integer yearsOfExperienceQuery = Integer.parseInt(rawQuery);
            Predicate yearsOfExperience = criteriaBuilder.equal(root.get("yearsOfExperience"), yearsOfExperienceQuery);
            return criteriaBuilder.or(roleName, yearsOfExperience);
        } catch (NumberFormatException e) {
            log.info("Query '{}' is not a valid year of experience", e.getMessage());
        }
        return roleName;
    }
}
