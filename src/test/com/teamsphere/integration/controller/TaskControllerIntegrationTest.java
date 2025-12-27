package com.teamsphere.integration.controller;

import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.dto.task.TaskSearchRequest;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.entity.EmployeeEntity;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.DepartmentRepository;
import com.teamsphere.repository.EmployeeRepository;
import com.teamsphere.repository.PositionRepository;
import com.teamsphere.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("TaskController Integration Tests")
class TaskControllerIntegrationTest extends BaseIntegrationTest {

    private static final String TASK_BASE_URL = "/api/v1/task";

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    private EmployeeEntity employee;
    private TaskEntity featureTask;
    private TaskEntity bugTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        positionRepository.deleteAll();

        // Create supporting entities
        DepartmentEntity department = DepartmentEntity.builder()
                .departmentName("Engineering")
                .description("Engineering Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        department = departmentRepository.save(department);

        PositionEntity position = PositionEntity.builder()
                .positionName("Developer")
                .yearsOfExperience(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        position = positionRepository.save(position);

        employee = EmployeeEntity.builder()
                .firstName("Task")
                .lastName("Owner")
                .pin("1234567890")
                .address("123 Work Street")
                .email("task.owner@company.com")
                .department(department)
                .position(position)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        employee = employeeRepository.save(employee);

        // Create tasks
        featureTask = TaskEntity.builder()
                .taskNumber("1001")
                .taskDescription("Implement user authentication feature")
                .taskStatus(TaskStatus.ACTIVE)
                .taskPriority(TaskPriority.HIGH)
                .taskType(TaskType.FEATURE)
                .timeSpentMinutes(120)
                .employee(employee)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        featureTask = taskRepository.save(featureTask);

        bugTask = TaskEntity.builder()
                .taskNumber("2001")
                .taskDescription("Fix login button styling issue")
                .taskStatus(TaskStatus.PENDING)
                .taskPriority(TaskPriority.MEDIUM)
                .taskType(TaskType.BUG)
                .timeSpentMinutes(30)
                .employee(employee)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bugTask = taskRepository.save(bugTask);
    }

    @Nested
    @DisplayName("GET /api/v1/task - Get All Tasks")
    class GetAllTasksTests {

        @Test
        @DisplayName("Should return all tasks for authenticated user")
        void getAllTasks_WithUserAuth_ShouldReturnTasks() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[*].taskDescription", 
                            containsInAnyOrder(
                                    "Implement user authentication feature",
                                    "Fix login button styling issue"
                            )));
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllTasks_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should support pagination")
        void getAllTasks_WithPagination_ShouldReturnPagedResults() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        @DisplayName("Should return tasks sorted by status priority")
        void getAllTasks_ShouldBeSortedByStatusPriority() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    // ACTIVE tasks should come before PENDING
                    .andExpect(jsonPath("$.content[0].taskStatus").value("ACTIVE"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/task/{id} - Get Task by ID")
    class GetTaskByIdTests {

        @Test
        @DisplayName("Should return task for valid ID")
        void getTaskById_WithValidId_ShouldReturnTask() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL + "/" + featureTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(featureTask.getId()))
                    .andExpect(jsonPath("$.taskDescription").value("Implement user authentication feature"))
                    .andExpect(jsonPath("$.taskStatus").value("ACTIVE"))
                    .andExpect(jsonPath("$.taskPriority").value("HIGH"))
                    .andExpect(jsonPath("$.taskType").value("FEATURE"))
                    .andExpect(jsonPath("$.timeSpentMinutes").value(120));
        }

        @Test
        @DisplayName("Should return 404 for non-existent task")
        void getTaskById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should include task number")
        void getTaskById_ShouldIncludeTaskNumber() throws Exception {
            mockMvc.perform(get(TASK_BASE_URL + "/" + bugTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.taskNumber").value("2001"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/task - Create Task")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task for admin user")
        void createTask_WithAdminAuth_ShouldCreateTask() throws Exception {
            TaskDto newTask = TaskDto.builder()
                    .taskDescription("Implement new dashboard component")
                    .taskStatus("PENDING")
                    .taskPriority("LOW")
                    .taskType("FEATURE")
                    .timeSpentMinutes(0)
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newTask)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.taskDescription").value("Implement new dashboard component"))
                    .andExpect(jsonPath("$.taskNumber").exists());  // Auto-generated
        }

        @Test
        @DisplayName("Should return 403 for regular user trying to create task")
        void createTask_WithUserAuth_ShouldReturnForbidden() throws Exception {
            TaskDto newTask = TaskDto.builder()
                    .taskDescription("New task description")
                    .taskStatus("PENDING")
                    .taskPriority("MEDIUM")
                    .taskType("BUG")
                    .timeSpentMinutes(0)
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newTask)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid task data")
        void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            TaskDto invalidTask = TaskDto.builder()
                    .taskDescription("")  // Empty - invalid
                    .taskStatus("")       // Empty - invalid
                    .taskPriority("")     // Empty - invalid
                    .taskType("")         // Empty - invalid
                    .timeSpentMinutes(-1) // Negative - invalid
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidTask)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when time spent exceeds maximum")
        void createTask_WithExcessiveTimeSpent_ShouldReturnBadRequest() throws Exception {
            TaskDto invalidTask = TaskDto.builder()
                    .taskDescription("Valid description")
                    .taskStatus("ACTIVE")
                    .taskPriority("HIGH")
                    .taskType("FEATURE")
                    .timeSpentMinutes(500)  // Exceeds 480 max
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidTask)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create REFACTOR task successfully")
        void createTask_WithRefactorType_ShouldSucceed() throws Exception {
            TaskDto newTask = TaskDto.builder()
                    .taskDescription("Refactor authentication module")
                    .taskStatus("PENDING")
                    .taskPriority("MEDIUM")
                    .taskType("REFACTOR")
                    .timeSpentMinutes(0)
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newTask)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.taskType").value("REFACTOR"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/task/{id} - Update Task")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task for admin user")
        void updateTask_WithAdminAuth_ShouldUpdateTask() throws Exception {
            TaskDto updatedTask = TaskDto.builder()
                    .taskDescription("Updated authentication feature - phase 2")
                    .taskStatus("ACTIVE")
                    .taskPriority("HIGH")
                    .taskType("FEATURE")
                    .timeSpentMinutes(180)
                    .build();

            mockMvc.perform(put(TASK_BASE_URL + "/" + featureTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(TASK_BASE_URL + "/" + featureTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.taskDescription").value("Updated authentication feature - phase 2"))
                    .andExpect(jsonPath("$.timeSpentMinutes").value(180));
        }

        @Test
        @DisplayName("Should update task status to FINISHED")
        void updateTask_ToFinished_ShouldSucceed() throws Exception {
            TaskDto updatedTask = TaskDto.builder()
                    .taskDescription("Implement user authentication feature")
                    .taskStatus("FINISHED")
                    .taskPriority("HIGH")
                    .taskType("FEATURE")
                    .timeSpentMinutes(240)
                    .build();

            mockMvc.perform(put(TASK_BASE_URL + "/" + featureTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(TASK_BASE_URL + "/" + featureTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.taskStatus").value("FINISHED"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/task/{id} - Delete Task")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete task for admin user")
        void deleteTask_WithAdminAuth_ShouldDeleteTask() throws Exception {
            mockMvc.perform(delete(TASK_BASE_URL + "/" + bugTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(TASK_BASE_URL + "/" + bugTask.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent task")
        void deleteTask_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(TASK_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/task/search - Search Tasks")
    class SearchTaskTests {

        @Test
        @DisplayName("Should search tasks by description for admin user")
        void searchTask_ByDescription_ShouldReturnMatchingTasks() throws Exception {
            TaskSearchRequest searchRequest = new TaskSearchRequest("authentication");

            mockMvc.perform(post(TASK_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].taskDescription", 
                            containsString("authentication")));
        }

        @Test
        @DisplayName("Should search tasks by task number")
        void searchTask_ByTaskNumber_ShouldReturnMatchingTasks() throws Exception {
            TaskSearchRequest searchRequest = new TaskSearchRequest("2001");

            mockMvc.perform(post(TASK_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].taskNumber").value("2001"));
        }

        @Test
        @DisplayName("Should return empty results for non-matching query")
        void searchTask_NoMatch_ShouldReturnEmpty() throws Exception {
            TaskSearchRequest searchRequest = new TaskSearchRequest("nonexistent-xyz");

            mockMvc.perform(post(TASK_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Business Logic")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle task with maximum allowed time spent")
        void createTask_WithMaxTimeSpent_ShouldSucceed() throws Exception {
            TaskDto newTask = TaskDto.builder()
                    .taskDescription("Long task description for maximum time")
                    .taskStatus("FINISHED")
                    .taskPriority("HIGH")
                    .taskType("FEATURE")
                    .timeSpentMinutes(480)  // Maximum allowed
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newTask)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeSpentMinutes").value(480));
        }

        @Test
        @DisplayName("Should handle task with zero time spent")
        void createTask_WithZeroTimeSpent_ShouldSucceed() throws Exception {
            TaskDto newTask = TaskDto.builder()
                    .taskDescription("New task just created")
                    .taskStatus("PENDING")
                    .taskPriority("LOW")
                    .taskType("BUG")
                    .timeSpentMinutes(0)
                    .build();

            mockMvc.perform(post(TASK_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newTask)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.timeSpentMinutes").value(0));
        }
    }
}

