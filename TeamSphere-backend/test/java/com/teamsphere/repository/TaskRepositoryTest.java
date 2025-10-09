package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private TaskEntity task;

    @BeforeEach
    void setUp() {
        task = new TaskEntity();
        task.setCreatedAt(java.time.LocalDateTime.now());
        task.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        task.setTaskNumber("TASK-001");
        task.setTaskDescription("Fix bug in authentication");
        task.setTaskPriority(TaskPriority.HIGH);
        task.setTaskStatus(TaskStatus.ACTIVE);
        task.setTaskType(TaskType.BUG);
        task.setTimeSpentMinutes(120);
    }

    @Test
    void save_shouldPersistTask() {
        // When
        TaskEntity saved = taskRepository.save(task);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTaskNumber()).isEqualTo("TASK-001");
        assertThat(saved.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(saved.getTaskStatus()).isEqualTo(TaskStatus.ACTIVE);
    }

    @Test
    void findById_shouldReturnTask() {
        // Given
        TaskEntity persisted = entityManager.persistAndFlush(task);

        // When
        Optional<TaskEntity> found = taskRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTaskNumber()).isEqualTo("TASK-001");
        assertThat(found.get().getTaskDescription()).isEqualTo("Fix bug in authentication");
    }

    @Test
    void findLastTaskByTaskType_shouldReturnLastTaskNumber() {
        // Given
        task.setTaskNumber("100");
        entityManager.persist(task);
        
        TaskEntity task2 = new TaskEntity();
        task2.setCreatedAt(java.time.LocalDateTime.now());
        task2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        task2.setTaskNumber("150");
        task2.setTaskDescription("Another bug");
        task2.setTaskPriority(TaskPriority.MEDIUM);
        task2.setTaskStatus(TaskStatus.PENDING);
        task2.setTaskType(TaskType.BUG);
        entityManager.persist(task2);
        entityManager.flush();

        // When
        Optional<Long> lastNumber = taskRepository.findLastTaskByTaskType(TaskType.BUG);

        // Then
        assertThat(lastNumber).isPresent();
        assertThat(lastNumber.get()).isEqualTo(150L);
    }

    @Test
    void findLastTaskByTaskType_whenNoTasks_shouldReturnEmpty() {
        // When
        Optional<Long> lastNumber = taskRepository.findLastTaskByTaskType(TaskType.FEATURE);

        // Then
        assertThat(lastNumber).isPresent();
        assertThat(lastNumber.get()).isEqualTo(0L);
    }

    @Test
    void findAllSorted_shouldReturnTasksSortedByStatus() {
        // Given
        TaskEntity activeTask = new TaskEntity();
        activeTask.setCreatedAt(java.time.LocalDateTime.now());
        activeTask.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        activeTask.setTaskNumber("TASK-002");
        activeTask.setTaskDescription("Active task");
        activeTask.setTaskPriority(TaskPriority.HIGH);
        activeTask.setTaskStatus(TaskStatus.ACTIVE);
        activeTask.setTaskType(TaskType.FEATURE);
        entityManager.persist(activeTask);

        TaskEntity pendingTask = new TaskEntity();
        pendingTask.setCreatedAt(java.time.LocalDateTime.now());
        pendingTask.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        pendingTask.setTaskNumber("TASK-003");
        pendingTask.setTaskDescription("Pending task");
        pendingTask.setTaskPriority(TaskPriority.MEDIUM);
        pendingTask.setTaskStatus(TaskStatus.PENDING);
        pendingTask.setTaskType(TaskType.REFACTOR);
        entityManager.persist(pendingTask);

        TaskEntity finishedTask = new TaskEntity();
        finishedTask.setCreatedAt(java.time.LocalDateTime.now());
        finishedTask.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        finishedTask.setTaskNumber("TASK-004");
        finishedTask.setTaskDescription("Finished task");
        finishedTask.setTaskPriority(TaskPriority.LOW);
        finishedTask.setTaskStatus(TaskStatus.FINISHED);
        finishedTask.setTaskType(TaskType.BUG);
        entityManager.persist(finishedTask);
        
        entityManager.flush();

        // When
        Page<TaskEntity> tasks = taskRepository.findAllSorted(PageRequest.of(0, 10));

        // Then
        assertThat(tasks.getContent()).hasSize(3);
        assertThat(tasks.getContent().get(0).getTaskStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(tasks.getContent().get(1).getTaskStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(tasks.getContent().get(2).getTaskStatus()).isEqualTo(TaskStatus.FINISHED);
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        // Given
        entityManager.persist(task);
        
        TaskEntity task2 = new TaskEntity();
        task2.setCreatedAt(java.time.LocalDateTime.now());
        task2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        task2.setTaskNumber("TASK-002");
        task2.setTaskDescription("Implement new feature");
        task2.setTaskPriority(TaskPriority.MEDIUM);
        task2.setTaskStatus(TaskStatus.PENDING);
        task2.setTaskType(TaskType.FEATURE);
        entityManager.persist(task2);
        entityManager.flush();

        // When
        List<TaskEntity> tasks = taskRepository.findAll();

        // Then
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(TaskEntity::getTaskNumber)
                .containsExactlyInAnyOrder("TASK-001", "TASK-002");
    }

    @Test
    void delete_shouldRemoveTask() {
        // Given
        TaskEntity persisted = entityManager.persistAndFlush(task);

        // When
        taskRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<TaskEntity> found = taskRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyTask() {
        // Given
        TaskEntity persisted = entityManager.persistAndFlush(task);

        // When
        persisted.setTaskStatus(TaskStatus.FINISHED);
        persisted.setTimeSpentMinutes(180);
        TaskEntity updated = taskRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getTaskStatus()).isEqualTo(TaskStatus.FINISHED);
        assertThat(updated.getTimeSpentMinutes()).isEqualTo(180);
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

