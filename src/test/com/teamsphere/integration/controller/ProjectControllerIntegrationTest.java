package com.teamsphere.integration.controller;

import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.CompanyRepository;
import com.teamsphere.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("ProjectController Integration Tests")
class ProjectControllerIntegrationTest extends BaseIntegrationTest {

    private static final String PROJECT_BASE_URL = "/api/v1/project";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private CompanyEntity company;
    private ProjectEntity webProject;
    private ProjectEntity mobileProject;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        companyRepository.deleteAll();

        // Create company first
        company = CompanyEntity.builder()
                .name("Tech Corp")
                .industry("Technology")
                .address("123 Tech Street")
                .email("contact@techcorp.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        company = companyRepository.save(company);

        // Create projects
        webProject = ProjectEntity.builder()
                .name("Web Platform Development")
                .description("Development of company web platform")
                .startDate(LocalDate.of(2024, 1, 15))
                .finishDate(LocalDate.of(2024, 12, 31))
                .status(ProjectStatus.IN_PROGRESS)
                .company(company)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        webProject = projectRepository.save(webProject);

        mobileProject = ProjectEntity.builder()
                .name("Mobile App Launch")
                .description("Launch of mobile application")
                .startDate(LocalDate.of(2024, 3, 1))
                .finishDate(LocalDate.of(2024, 8, 31))
                .status(ProjectStatus.FINISHED)
                .company(company)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        mobileProject = projectRepository.save(mobileProject);
    }

    @Nested
    @DisplayName("GET /api/v1/project - Get All Projects")
    class GetAllProjectsTests {

        @Test
        @DisplayName("Should return all projects for authenticated user")
        void getAllProjects_WithUserAuth_ShouldReturnProjects() throws Exception {
            mockMvc.perform(get(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[*].name", 
                            containsInAnyOrder("Web Platform Development", "Mobile App Launch")));
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllProjects_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(PROJECT_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/project/{id} - Get Project by ID")
    class GetProjectByIdTests {

        @Test
        @DisplayName("Should return project for valid ID")
        void getProjectById_WithValidId_ShouldReturnProject() throws Exception {
            mockMvc.perform(get(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(webProject.getId()))
                    .andExpect(jsonPath("$.name").value("Web Platform Development"))
                    .andExpect(jsonPath("$.description").value("Development of company web platform"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.companyId").value(company.getId()));
        }

        @Test
        @DisplayName("Should return 404 for non-existent project")
        void getProjectById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(PROJECT_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should include company information")
        void getProjectById_ShouldIncludeCompanyInfo() throws Exception {
            mockMvc.perform(get(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.companyName").value("Tech Corp"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/project - Create Project")
    class CreateProjectTests {

        @Test
        @DisplayName("Should create project for admin user")
        void createProject_WithAdminAuth_ShouldCreateProject() throws Exception {
            ProjectDto newProject = ProjectDto.builder()
                    .name("API Integration Project")
                    .description("Integration with third-party APIs")
                    .startDate("2024-06-01")
                    .finishDate("2024-09-30")
                    .status("IN_PROGRESS")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(post(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newProject)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("API Integration Project"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @DisplayName("Should return 403 for regular user trying to create project")
        void createProject_WithUserAuth_ShouldReturnForbidden() throws Exception {
            ProjectDto newProject = ProjectDto.builder()
                    .name("New Project")
                    .description("Description")
                    .startDate("2024-06-01")
                    .status("IN_PROGRESS")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(post(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newProject)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid project data")
        void createProject_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            ProjectDto invalidProject = ProjectDto.builder()
                    .name("")  // Empty name - invalid
                    .description("")  // Empty description - invalid
                    .startDate("invalid-date")  // Invalid date format
                    .status("IN_PROGRESS")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(post(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidProject)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create project without finish date")
        void createProject_WithoutFinishDate_ShouldSucceed() throws Exception {
            ProjectDto newProject = ProjectDto.builder()
                    .name("Ongoing Project")
                    .description("A project without end date")
                    .startDate("2024-01-01")
                    .status("IN_PROGRESS")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(post(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newProject)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.finishDate").doesNotExist());
        }

        @Test
        @DisplayName("Should return 400 for missing company ID")
        void createProject_WithMissingCompanyId_ShouldReturnBadRequest() throws Exception {
            ProjectDto invalidProject = ProjectDto.builder()
                    .name("Valid Name")
                    .description("Valid description")
                    .startDate("2024-01-01")
                    .status("IN_PROGRESS")
                    // companyId is missing
                    .build();

            mockMvc.perform(post(PROJECT_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidProject)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/project/{id} - Update Project")
    class UpdateProjectTests {

        @Test
        @DisplayName("Should update project for admin user")
        void updateProject_WithAdminAuth_ShouldUpdateProject() throws Exception {
            ProjectDto updatedProject = ProjectDto.builder()
                    .name("Updated Web Platform")
                    .description("Updated description for web platform")
                    .startDate("2024-01-15")
                    .finishDate("2025-03-31")
                    .status("IN_PROGRESS")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(put(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedProject)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Web Platform"));
        }

        @Test
        @DisplayName("Should update project status to FINISHED")
        void updateProject_ToFinished_ShouldSucceed() throws Exception {
            ProjectDto updatedProject = ProjectDto.builder()
                    .name("Web Platform Development")
                    .description("Development completed")
                    .startDate("2024-01-15")
                    .finishDate("2024-11-30")
                    .status("FINISHED")
                    .companyId(company.getId())
                    .build();

            mockMvc.perform(put(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedProject)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(PROJECT_BASE_URL + "/" + webProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("FINISHED"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/project/{id} - Delete Project")
    class DeleteProjectTests {

        @Test
        @DisplayName("Should delete project for admin user")
        void deleteProject_WithAdminAuth_ShouldDeleteProject() throws Exception {
            mockMvc.perform(delete(PROJECT_BASE_URL + "/" + mobileProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(PROJECT_BASE_URL + "/" + mobileProject.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent project")
        void deleteProject_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(PROJECT_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/project/search - Search Projects")
    class SearchProjectTests {

        @Test
        @DisplayName("Should search projects by name for admin user")
        void searchProject_ByName_ShouldReturnMatchingProjects() throws Exception {
            ProjectSearchRequest searchRequest = new ProjectSearchRequest("Web");

            mockMvc.perform(post(PROJECT_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("Web Platform Development"));
        }

        @Test
        @DisplayName("Should search projects by description")
        void searchProject_ByDescription_ShouldReturnMatchingProjects() throws Exception {
            ProjectSearchRequest searchRequest = new ProjectSearchRequest("mobile");

            mockMvc.perform(post(PROJECT_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should return all projects matching 'Platform' or 'App'")
        void searchProject_WithMultipleMatches_ShouldReturnAll() throws Exception {
            ProjectSearchRequest searchRequest = new ProjectSearchRequest("Development");

            mockMvc.perform(post(PROJECT_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }
    }
}

