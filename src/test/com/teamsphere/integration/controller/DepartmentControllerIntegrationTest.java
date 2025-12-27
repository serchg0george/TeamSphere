package com.teamsphere.integration.controller;

import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.DepartmentRepository;
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
 * Integration tests for DepartmentController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("DepartmentController Integration Tests")
class DepartmentControllerIntegrationTest extends BaseIntegrationTest {

    private static final String DEPARTMENT_BASE_URL = "/api/v1/department";

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentEntity engineeringDepartment;
    private DepartmentEntity hrDepartment;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();

        engineeringDepartment = DepartmentEntity.builder()
                .departmentName("Engineering")
                .description("Software development and engineering department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        engineeringDepartment = departmentRepository.save(engineeringDepartment);

        hrDepartment = DepartmentEntity.builder()
                .departmentName("Human Resources")
                .description("HR and employee management")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        hrDepartment = departmentRepository.save(hrDepartment);
    }

    @Nested
    @DisplayName("GET /api/v1/department - Get All Departments")
    class GetAllDepartmentsTests {

        @Test
        @DisplayName("Should return all departments for authenticated user")
        void getAllDepartments_WithUserAuth_ShouldReturnDepartments() throws Exception {
            mockMvc.perform(get(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[*].departmentName", 
                            containsInAnyOrder("Engineering", "Human Resources")));
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllDepartments_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(DEPARTMENT_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should support pagination")
        void getAllDepartments_WithPagination_ShouldReturnPagedResults() throws Exception {
            mockMvc.perform(get(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/department/{id} - Get Department by ID")
    class GetDepartmentByIdTests {

        @Test
        @DisplayName("Should return department for valid ID")
        void getDepartmentById_WithValidId_ShouldReturnDepartment() throws Exception {
            mockMvc.perform(get(DEPARTMENT_BASE_URL + "/" + engineeringDepartment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(engineeringDepartment.getId()))
                    .andExpect(jsonPath("$.departmentName").value("Engineering"))
                    .andExpect(jsonPath("$.description").value("Software development and engineering department"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent department")
        void getDepartmentById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(DEPARTMENT_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/department - Create Department")
    class CreateDepartmentTests {

        @Test
        @DisplayName("Should create department for admin user")
        void createDepartment_WithAdminAuth_ShouldCreateDepartment() throws Exception {
            DepartmentDto newDepartment = DepartmentDto.builder()
                    .departmentName("Marketing")
                    .description("Marketing and brand management")
                    .build();

            mockMvc.perform(post(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newDepartment)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.departmentName").value("Marketing"));
        }

        @Test
        @DisplayName("Should return 403 for regular user trying to create department")
        void createDepartment_WithUserAuth_ShouldReturnForbidden() throws Exception {
            DepartmentDto newDepartment = DepartmentDto.builder()
                    .departmentName("Sales")
                    .description("Sales department")
                    .build();

            mockMvc.perform(post(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newDepartment)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid department data")
        void createDepartment_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            DepartmentDto invalidDepartment = DepartmentDto.builder()
                    .departmentName("")  // Empty name - invalid
                    .description("")     // Empty description - invalid
                    .build();

            mockMvc.perform(post(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDepartment)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when department name exceeds max length")
        void createDepartment_WithTooLongName_ShouldReturnBadRequest() throws Exception {
            DepartmentDto invalidDepartment = DepartmentDto.builder()
                    .departmentName("A".repeat(50))  // Exceeds 40 char limit
                    .description("Valid description")
                    .build();

            mockMvc.perform(post(DEPARTMENT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDepartment)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/department/{id} - Update Department")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("Should update department for admin user")
        void updateDepartment_WithAdminAuth_ShouldUpdateDepartment() throws Exception {
            DepartmentDto updatedDepartment = DepartmentDto.builder()
                    .departmentName("Software Engineering")
                    .description("Updated engineering department description")
                    .build();

            mockMvc.perform(put(DEPARTMENT_BASE_URL + "/" + engineeringDepartment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedDepartment)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(DEPARTMENT_BASE_URL + "/" + engineeringDepartment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Software Engineering"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/department/{id} - Delete Department")
    class DeleteDepartmentTests {

        @Test
        @DisplayName("Should delete department for admin user")
        void deleteDepartment_WithAdminAuth_ShouldDeleteDepartment() throws Exception {
            mockMvc.perform(delete(DEPARTMENT_BASE_URL + "/" + hrDepartment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(DEPARTMENT_BASE_URL + "/" + hrDepartment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent department")
        void deleteDepartment_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(DEPARTMENT_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/department/search - Search Departments")
    class SearchDepartmentTests {

        @Test
        @DisplayName("Should search departments by name for admin user")
        void searchDepartment_ByName_ShouldReturnMatchingDepartments() throws Exception {
            DepartmentSearchRequest searchRequest = new DepartmentSearchRequest("Engineering");

            mockMvc.perform(post(DEPARTMENT_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].departmentName").value("Engineering"));
        }

        @Test
        @DisplayName("Should search departments by description")
        void searchDepartment_ByDescription_ShouldReturnMatchingDepartments() throws Exception {
            DepartmentSearchRequest searchRequest = new DepartmentSearchRequest("HR");

            mockMvc.perform(post(DEPARTMENT_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }
    }
}

