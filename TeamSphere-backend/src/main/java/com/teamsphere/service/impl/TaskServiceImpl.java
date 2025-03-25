package com.teamsphere.service.impl;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.mapper.TaskMapper;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.TaskRepository;
import com.teamsphere.service.TaskService;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TaskServiceImpl extends GenericServiceImpl<TaskEntity, TaskDto> implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final EntityManager entityManager;

    @Override
    public BaseMapper<TaskEntity, TaskDto> getMapper() {
        return taskMapper;
    }

    @Override
    public JpaRepository<TaskEntity, Long> getRepository() {
        return taskRepository;
    }

    @Override
    public Page<TaskDto> getAll(Pageable pageable) {
        return taskRepository.findAllSorted(pageable).map(taskMapper::toDto);
    }

    @Override
    public TaskDto save(TaskDto dto) {
        Long lastNumber = taskRepository.findLastTaskByTaskType(TaskType.valueOf(dto.getTaskType())).orElse(0L);

        TaskEntity taskEntity = TaskEntity.builder()
                .taskStatus(TaskStatus.valueOf(dto.getTaskStatus()))
                .taskPriority(TaskPriority.valueOf(dto.getTaskPriority()))
                .taskType(TaskType.valueOf(dto.getTaskType()))
                .timeSpentMinutes(dto.getTimeSpentMinutes())
                .taskDescription(dto.getTaskDescription())
                .taskNumber(String.valueOf(lastNumber + 1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskEntity saved = taskRepository.save(taskEntity);

        return taskMapper.toDto(saved);
    }

    @Override
    public Page<TaskDto> find(final TaskSearchRequest request, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskEntity> criteriaQuery = criteriaBuilder.createQuery(TaskEntity.class);
        Root<TaskEntity> root = criteriaQuery.from(TaskEntity.class);

        String query = "%" + request.query() + "%";
        Predicate mainPredicate = buildPredicates(criteriaBuilder, query, root);
        criteriaQuery.where(mainPredicate);

        TypedQuery<TaskEntity> tQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<TaskEntity> resultList = tQuery.getResultList();
        List<TaskDto> dtoList = resultList.stream()
                .map(taskMapper::toDto)
                .toList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<TaskEntity> countRoot = countQuery.from(TaskEntity.class);
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

    private Predicate buildPredicates(final CriteriaBuilder criteriaBuilder, final String query, final Root<TaskEntity> root) {
        Predicate taskDescription = criteriaBuilder.like(root.get("taskDescription"), query);
        Predicate taskNumber = criteriaBuilder.like(root.get("taskNumber"), query);
        Predicate taskStatus = criteriaBuilder.like(root.get("taskStatus"), query.toUpperCase());
        try {
            Integer timeSpentMinutesQuery = Integer.parseInt(query);
            Predicate timeSpentMinutes = criteriaBuilder.equal(root.get("timeSpentMinutes"), timeSpentMinutesQuery);
            return criteriaBuilder.or(timeSpentMinutes, taskDescription, taskNumber, taskStatus);
        } catch (NumberFormatException e) {
            log.info("Query '{}' is not a valid number", e.getMessage());
        }
        return criteriaBuilder.or(taskDescription, taskNumber, taskStatus);
    }

}
