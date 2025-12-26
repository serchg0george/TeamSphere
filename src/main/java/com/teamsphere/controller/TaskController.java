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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task")
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/search")
    public ResponseEntity<Page<TaskDto>> searchTask(@RequestBody TaskSearchRequest findTask,
                                                    Pageable pageable) {
        return ResponseEntity.ok(taskService.find(findTask, pageable));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto task) {
        TaskDto created = taskService.save(task);
        URI location = URI.create("/api/v1/task/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id") Long taskId) {
        return ResponseEntity.ok(taskService.get(taskId));
    }

    @GetMapping
    public ResponseEntity<Page<TaskDto>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateTask(@PathVariable("id") Long taskId,
                                           @Valid @RequestBody TaskDto task) {
        taskService.update(task, taskId);
        return ResponseEntity.noContent().build();
    }

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
