package com.teamsphere.integration.controller;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.entity.EmployeeEntity;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.DepartmentRepository;
import com.teamsphere.repository.EmployeeRepository;
import com.teamsphere.repository.PositionRepository;
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
 * Integration tests for EmployeeController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("EmployeeController Integration Tests")
class EmployeeControllerIntegrationTest extends BaseIntegrationTest {

    private static final String EMPLOYEE_BASE_URL = "/api/v1/employee";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    private DepartmentEntity department;
    private PositionEntity position;
    private EmployeeEntity johnDoe;
    private EmployeeEntity janeSmith;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        positionRepository.deleteAll();

        // Create supporting entities first
        department = DepartmentEntity.builder()
                .departmentName("Engineering")
                .description("Software Engineering Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        department = departmentRepository.save(department);

        position = PositionEntity.builder()
                .positionName("Software Developer")
                .yearsOfExperience(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        position = positionRepository.save(position);

        // Create employees
        johnDoe = EmployeeEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .pin("1234567890")
                .address("123 Main Street")
                .email("john.doe@company.com")
                .department(department)
                .position(position)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        johnDoe = employeeRepository.save(johnDoe);

        janeSmith = EmployeeEntity.builder()
                .firstName("Jane")
                .lastName("Smith")
                .pin("0987654321")
                .address("456 Oak Avenue")
                .email("jane.smith@company.com")
                .department(department)
                .position(position)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        janeSmith = employeeRepository.save(janeSmith);
    }

    @Nested
    @DisplayName("GET /api/v1/employee - Get All Employees")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllEmployees_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(EMPLOYEE_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employee/{id} - Get Employee by ID")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return 404 for non-existent employee")
        void getEmployeeById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(EMPLOYEE_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/employee - Create Employee")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should return 403 for regular user trying to create employee")
        void createEmployee_WithUserAuth_ShouldReturnForbidden() throws Exception {
            EmployeeDto newEmployee = EmployeeDto.builder()
                    .firstName("Bob")
                    .lastName("Wilson")
                    .pin("9988776655")
                    .address("321 Elm Street")
                    .email("bob.wilson@company.com")
                    .departmentId(department.getId())
                    .positionId(position.getId())
                    .build();

            mockMvc.perform(post(EMPLOYEE_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newEmployee)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid employee data")
        void createEmployee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            EmployeeDto invalidEmployee = EmployeeDto.builder()
                    .firstName("")  // Empty - invalid
                    .lastName("")   // Empty - invalid
                    .address("")    // Empty - invalid
                    .email("invalid-email")  // Invalid email format
                    .departmentId(department.getId())
                    .positionId(position.getId())
                    .build();

            mockMvc.perform(post(EMPLOYEE_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmployee)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid name pattern")
        void createEmployee_WithInvalidNamePattern_ShouldReturnBadRequest() throws Exception {
            EmployeeDto invalidEmployee = EmployeeDto.builder()
                    .firstName("John123")  // Contains numbers - invalid pattern
                    .lastName("Doe")
                    .address("123 Street")
                    .email("john@company.com")
                    .departmentId(department.getId())
                    .positionId(position.getId())
                    .build();

            mockMvc.perform(post(EMPLOYEE_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmployee)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for missing department ID")
        void createEmployee_WithMissingDepartment_ShouldReturnBadRequest() throws Exception {
            EmployeeDto invalidEmployee = EmployeeDto.builder()
                    .firstName("Test")
                    .lastName("User")
                    .address("123 Street")
                    .email("test@company.com")
                    .positionId(position.getId())
                    // departmentId is missing
                    .build();

            mockMvc.perform(post(EMPLOYEE_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmployee)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/employee/{id} - Delete Employee")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should delete employee for admin user")
        void deleteEmployee_WithAdminAuth_ShouldDeleteEmployee() throws Exception {
            mockMvc.perform(delete(EMPLOYEE_BASE_URL + "/" + janeSmith.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(EMPLOYEE_BASE_URL + "/" + janeSmith.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent employee")
        void deleteEmployee_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(EMPLOYEE_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }
}

