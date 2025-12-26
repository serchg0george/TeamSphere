package com.teamsphere.controller;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for task management operations.
 * Provides endpoints for CRUD operations and search functionality for tasks.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task")
@Validated
public class TaskController {

    private final TaskService taskService;

    /**
     * Searches for tasks based on search criteria with pagination.
     *
     * @param findTask the search criteria for tasks
     * @param pageable pagination information
     * @return ResponseEntity containing a page of matching tasks
     */
    @PostMapping("/search")
    public ResponseEntity<Page<TaskDto>> searchTask(@RequestBody TaskSearchRequest findTask,
                                                    Pageable pageable) {
        return ResponseEntity.ok(taskService.find(findTask, pageable));
    }

    /**
     * Creates a new task.
     *
     * @param task the task data to create
     * @return ResponseEntity containing the created task with location header
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto task) {
        TaskDto created = taskService.save(task);
        URI location = URI.create("/api/v1/task/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId the ID of the task to retrieve
     * @return ResponseEntity containing the task data
     */
    @GetMapping("{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id") Long taskId) {
        return ResponseEntity.ok(taskService.get(taskId));
    }

    /**
     * Retrieves all tasks with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all tasks
     */
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable));
    }

    /**
     * Updates an existing task.
     *
     * @param taskId the ID of the task to update
     * @param task the updated task data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updateTask(@PathVariable("id") Long taskId,
                                           @Valid @RequestBody TaskDto task) {
        taskService.update(task, taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId the ID of the task to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId) {
        try {
            taskService.delete(taskId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
