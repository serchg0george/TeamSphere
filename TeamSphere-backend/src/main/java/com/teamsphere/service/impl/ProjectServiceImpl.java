package com.teamsphere.service.impl;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.dto.project.ProjectSearchResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public ProjectSearchResponse findProject(final ProjectSearchRequest request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> criteriaQuery = criteriaBuilder.createQuery(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<ProjectEntity> root = criteriaQuery.from(ProjectEntity.class);

        String query = "%" + request.query() + "%";
        Predicate name = criteriaBuilder.like(root.get("name"), query);
        Predicate description = criteriaBuilder.like(root.get("description"), query);

        List<Predicate> datePredicates = new ArrayList<>();
        boolean isDateQuery = false;
        try {
            LocalDate queryDate = LocalDate.parse(request.query(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            datePredicates.add(criteriaBuilder.equal(root.get("startDate"), queryDate));
            datePredicates.add(criteriaBuilder.equal(root.get("finishDate"), queryDate));
            isDateQuery = true;
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date: {}", request.query(), e);
        }

        Predicate status = null;
        if (!isDateQuery) {
            try {
                ProjectStatus projectStatus = ProjectStatus.valueOf(request.query().toUpperCase());
                status = criteriaBuilder.equal(root.get("status"), projectStatus);
            } catch (IllegalArgumentException e) {
                log.info("Query '{}' is not a valid project status", request.query());
            }
        }

        List<Predicate> allPredicates = new ArrayList<>();
        allPredicates.add(name);
        allPredicates.add(description);
        allPredicates.addAll(datePredicates);
        if (status != null) {
            allPredicates.add(status);
        }

        predicates.add(criteriaBuilder.or(allPredicates.toArray(new Predicate[0])));


        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));

        TypedQuery<ProjectEntity> tQuery = entityManager.createQuery(criteriaQuery);

        ProjectSearchResponse response = new ProjectSearchResponse();

        var projects = tQuery.getResultList().stream().map(projectMapper::toDto).toList();

        response.setProjects(projects);
        response.setProjectCount(projects.size());

        log.debug("Found {} projects for query '{}'", response.getProjectCount(), request.query());

        return response;
    }
}
