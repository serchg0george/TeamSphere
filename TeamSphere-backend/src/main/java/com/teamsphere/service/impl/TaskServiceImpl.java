package com.teamsphere.service.impl;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.dto.task.TaskSearchResponse;
import com.teamsphere.entity.TaskEntity;
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public TaskSearchResponse findTask(final TaskSearchRequest request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskEntity> criteriaQuery = criteriaBuilder.createQuery(TaskEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<TaskEntity> root = criteriaQuery.from(TaskEntity.class);

        String query = "%" + request.query() + "%";
        Predicate taskDescription = criteriaBuilder.like(root.get("taskDescription"), query);
        Predicate taskNumber = criteriaBuilder.like(root.get("taskNumber"), query);
        try {
            Integer timeSpentMinutesQuery = Integer.parseInt(request.query());
            Predicate timeSpentMinutes = criteriaBuilder.equal(root.get("timeSpentMinutes"), timeSpentMinutesQuery);
            predicates.add(criteriaBuilder.or(timeSpentMinutes, taskDescription, taskNumber));
        } catch (NumberFormatException e) {
            log.info("Query '{}' is not a valid number", e.getMessage());
        }
        predicates.add(taskDescription);
        predicates.add(taskNumber);

        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<TaskEntity> tQuery = entityManager.createQuery(criteriaQuery);

        TaskSearchResponse response = new TaskSearchResponse();

        var timeSheets = tQuery.getResultList().stream().map(taskMapper::toDto).toList();

        response.setTasks(timeSheets);
        response.setTaskCount(timeSheets.size());

        log.debug("Found {} projects for query '{}'", response.getTaskCount(), request.query());

        return response;
    }

}
