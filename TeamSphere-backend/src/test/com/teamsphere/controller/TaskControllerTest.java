package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = TaskController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDto taskDto;
    private List<TaskDto> taskList;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .id(1L)
                .taskNumber("TASK-001")
                .taskDescription("Fix bug in authentication")
                .taskPriority("HIGH")
                .taskStatus("ACTIVE")
                .taskType("BUG")
                .timeSpentMinutes(120)
                .build();

        TaskDto taskDto2 = TaskDto.builder()
                .id(2L)
                .taskNumber("TASK-002")
                .taskDescription("Implement new feature")
                .taskPriority("MEDIUM")
                .taskStatus("PENDING")
                .taskType("FEATURE")
                .timeSpentMinutes(0)
                .build();

        taskList = Arrays.asList(taskDto, taskDto2);
    }

    @Test
    void searchTask_shouldReturnPageOfTasks() throws Exception {
        // Given
        TaskSearchRequest searchRequest = new TaskSearchRequest("authentication");
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskDto> page = new PageImpl<>(List.of(taskDto), pageable, 1);
        when(taskService.find(any(TaskSearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/task/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].taskNumber").value("TASK-001"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(taskService, times(1)).find(any(TaskSearchRequest.class), any(Pageable.class));
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        // Given
        TaskDto newTask = TaskDto.builder()
                .taskNumber("TASK-003")
                .taskDescription("New task")
                .taskPriority("LOW")
                .taskStatus("PENDING")
                .taskType("REFACTOR")
                .timeSpentMinutes(0).build();

        when(taskService.save(any(TaskDto.class))).thenReturn(taskDto);

        // When & Then
        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskNumber").value("TASK-001"));

        verify(taskService, times(1)).save(any(TaskDto.class));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        // Given
        when(taskService.get(1L)).thenReturn(taskDto);

        // When & Then
        mockMvc.perform(get("/api/v1/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskNumber").value("TASK-001"))
                .andExpect(jsonPath("$.taskDescription").value("Fix bug in authentication"))
                .andExpect(jsonPath("$.taskPriority").value("HIGH"))
                .andExpect(jsonPath("$.taskStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.taskType").value("BUG"));

        verify(taskService, times(1)).get(1L);
    }

    @Test
    void getAllTasks_shouldReturnPageOfTasks() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskDto> page = new PageImpl<>(taskList, pageable, taskList.size());
        when(taskService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/task")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].taskNumber").value("TASK-001"))
                .andExpect(jsonPath("$.content[1].taskNumber").value("TASK-002"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(taskService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void updateTask_shouldReturnNoContent() throws Exception {
        // Given
        when(taskService.update(any(TaskDto.class), eq(1L))).thenReturn(taskDto);

        // When & Then
        mockMvc.perform(put("/api/v1/task/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).update(any(TaskDto.class), eq(1L));
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(taskService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/task/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).delete(1L);
    }

    @Test
    void deleteTask_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(taskService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/task/1"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).delete(1L);
    }
}

