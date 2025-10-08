package com.teamsphere.service.impl;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.mapper.TaskMapper;
import com.teamsphere.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskDto taskDto;
    private TaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .id(1L)
                .taskNumber("TASK-1")
                .taskDescription("Test Task")
                .taskStatus(TaskStatus.PENDING.toString())
                .taskPriority(TaskPriority.MEDIUM.toString())
                .taskType(TaskType.FEATURE.toString())
                .timeSpentMinutes(120)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTaskNumber("TASK-1");
        taskEntity.setTaskDescription("Test Task");
        taskEntity.setTaskStatus(TaskStatus.PENDING);
        taskEntity.setTaskPriority(TaskPriority.MEDIUM);
        taskEntity.setTaskType(TaskType.FEATURE);
        taskEntity.setTimeSpentMinutes(120);
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskEntity> page = new PageImpl<>(Collections.singletonList(taskEntity));
        when(taskRepository.findAllSorted(pageable)).thenReturn(page);
        when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(taskDto, result.getContent().get(0));
    }

    @Test
    void testSave() {
        when(taskRepository.findLastTaskByTaskType(TaskType.FEATURE)).thenReturn(Optional.of(0L));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

        TaskDto result = taskService.save(taskDto);

        assertNotNull(result);
        assertEquals(taskDto.getTaskNumber(), result.getTaskNumber());
    }

    @Test
    void testFind() {
        TaskSearchRequest request = new TaskSearchRequest("Test");
        Pageable pageable = PageRequest.of(0, 10);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<TaskEntity> criteriaQuery = mock(CriteriaQuery.class);
        Root<TaskEntity> root = mock(Root.class);
        Predicate predicate = mock(Predicate.class);
        TypedQuery<TaskEntity> typedQuery = mock(TypedQuery.class);
        CriteriaQuery<Long> countQuery = mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(TaskEntity.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(TaskEntity.class)).thenReturn(root);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(any(int.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(taskEntity));
        when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(TaskEntity.class)).thenReturn(root);
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(any(Predicate.class))).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(1L);

        Page<TaskDto> result = taskService.find(request, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(taskDto, result.getContent().get(0));
    }
}