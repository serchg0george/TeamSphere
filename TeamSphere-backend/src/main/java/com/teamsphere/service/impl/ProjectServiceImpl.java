package com.teamsphere.service.impl;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.mapper.ProjectMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.ProjectRepository;
import com.teamsphere.service.ProjectService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProjectServiceImpl extends GenericServiceImpl<ProjectEntity, ProjectDto> implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<ProjectEntity, ProjectDto> getMapper() {
        return projectMapper;
    }

    @Override
    public JpaRepository<ProjectEntity, Long> getRepository() {
        return projectRepository;
    }

    @Override
    public Page<ProjectDto> getAll(Pageable page) {
        List<ProjectEntity> projects = projectRepository.findAllWithCompanies();
        return new PageImpl<>(projects.stream().map(projectMapper::toDto).toList(), page, projects.size());
    }

    @Override
    public Page<ProjectDto> findProject(final ProjectSearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> criteriaQuery = criteriaBuilder.createQuery(ProjectEntity.class);
        Root<ProjectEntity> root = criteriaQuery.from(ProjectEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root);
        criteriaQuery.where(mainPredicate);

        TypedQuery<ProjectEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<ProjectEntity> resultList = tQuery.getResultList();
        List<ProjectDto> dtoList = resultList.stream()
                .map(projectMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<ProjectEntity> countRoot = countQuery.from(ProjectEntity.class);
        Predicate countPredicate = buildPredicates(criteriaBuilder, query, countRoot);

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicate);

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "id")));

        log.debug("Found {} projects for query '{}'", resultList.size(), request.query());

        return new PageImpl<>(dtoList, sorted, totalCount);
    }

    private Predicate buildPredicates(final CriteriaBuilder criteriaBuilder, final String query, final Root<ProjectEntity> root) {
        Predicate name = criteriaBuilder.like(root.get("name"), query);
        Predicate description = criteriaBuilder.like(root.get("description"), query);

        List<Predicate> datePredicates = new ArrayList<>();
        boolean isDateQuery = false;
        try {
            LocalDate queryDate = LocalDate.parse(query, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            datePredicates.add(criteriaBuilder.equal(root.get("startDate"), queryDate));
            datePredicates.add(criteriaBuilder.equal(root.get("finishDate"), queryDate));
            isDateQuery = true;
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date: {}", query, e);
        }

        Predicate status = null;
        if (!isDateQuery) {
            try {
                ProjectStatus projectStatus = ProjectStatus.valueOf(query.toUpperCase());
                status = criteriaBuilder.equal(root.get("status"), projectStatus);
                return criteriaBuilder.or(name, description, datePredicates.getFirst(), datePredicates.getLast(), status);
            } catch (IllegalArgumentException e) {
                log.info("Query '{}' is not a valid project status", query);
            }
        }

        return criteriaBuilder.or(name, description);
    }
}
