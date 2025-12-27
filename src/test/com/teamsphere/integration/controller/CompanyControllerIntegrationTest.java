package com.teamsphere.integration.controller;

import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.CompanyRepository;
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
 * Integration tests for CompanyController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("CompanyController Integration Tests")
class CompanyControllerIntegrationTest extends BaseIntegrationTest {

    private static final String COMPANY_BASE_URL = "/api/v1/company";

    @Autowired
    private CompanyRepository companyRepository;

    private CompanyEntity testCompany;
    private CompanyEntity secondCompany;

    @BeforeEach
    void setUp() {
        // Clear existing companies
        companyRepository.deleteAll();

        // Create test companies
        testCompany = CompanyEntity.builder()
                .name("Tech Solutions Inc")
                .industry("Technology")
                .address("123 Tech Street, Silicon Valley")
                .email("contact@techsolutions.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testCompany = companyRepository.save(testCompany);

        secondCompany = CompanyEntity.builder()
                .name("Finance Corp")
                .industry("Finance")
                .address("456 Wall Street, New York")
                .email("info@financecorp.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        secondCompany = companyRepository.save(secondCompany);
    }

    @Nested
    @DisplayName("GET /api/v1/company - Get All Companies")
    class GetAllCompaniesTests {

        @Test
        @DisplayName("Should return all companies for authenticated user")
        void getAllCompanies_WithUserAuth_ShouldReturnCompanies() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Tech Solutions Inc", "Finance Corp")))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        @DisplayName("Should return all companies for admin user")
        void getAllCompanies_WithAdminAuth_ShouldReturnCompanies() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllCompanies_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should support pagination")
        void getAllCompanies_WithPagination_ShouldReturnPagedResults() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/company/{id} - Get Company by ID")
    class GetCompanyByIdTests {

        @Test
        @DisplayName("Should return company for valid ID")
        void getCompanyById_WithValidId_ShouldReturnCompany() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testCompany.getId()))
                    .andExpect(jsonPath("$.name").value("Tech Solutions Inc"))
                    .andExpect(jsonPath("$.industry").value("Technology"))
                    .andExpect(jsonPath("$.address").value("123 Tech Street, Silicon Valley"))
                    .andExpect(jsonPath("$.email").value("contact@techsolutions.com"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent company")
        void getCompanyById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getCompanyById_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(COMPANY_BASE_URL + "/" + testCompany.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/company - Create Company")
    class CreateCompanyTests {

        @Test
        @DisplayName("Should create company for admin user")
        void createCompany_WithAdminAuth_ShouldCreateCompany() throws Exception {
            CompanyDto newCompany = CompanyDto.builder()
                    .name("New Startup")
                    .industry("Healthcare")
                    .address("789 Health Ave")
                    .email("contact@newstartup.com")
                    .build();

            mockMvc.perform(post(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newCompany)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("New Startup"))
                    .andExpect(jsonPath("$.industry").value("Healthcare"));
        }

        @Test
        @DisplayName("Should return 403 for regular user trying to create company")
        void createCompany_WithUserAuth_ShouldReturnForbidden() throws Exception {
            CompanyDto newCompany = CompanyDto.builder()
                    .name("New Startup")
                    .industry("Healthcare")
                    .address("789 Health Ave")
                    .email("contact@newstartup.com")
                    .build();

            mockMvc.perform(post(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newCompany)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid company data")
        void createCompany_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            CompanyDto invalidCompany = CompanyDto.builder()
                    .name("")  // Empty name
                    .industry("Healthcare")
                    .address("789 Health Ave")
                    .email("invalid-email")  // Invalid email
                    .build();

            mockMvc.perform(post(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidCompany)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for missing required fields")
        void createCompany_WithMissingFields_ShouldReturnBadRequest() throws Exception {
            CompanyDto incompleteCompany = CompanyDto.builder()
                    .name("Only Name")
                    .build();

            mockMvc.perform(post(COMPANY_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(incompleteCompany)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/company/{id} - Update Company")
    class UpdateCompanyTests {

        @Test
        @DisplayName("Should update company for admin user")
        void updateCompany_WithAdminAuth_ShouldUpdateCompany() throws Exception {
            CompanyDto updatedCompany = CompanyDto.builder()
                    .name("Updated Tech Solutions")
                    .industry("Software")
                    .address("New Address 123")
                    .email("updated@techsolutions.com")
                    .build();

            mockMvc.perform(put(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedCompany)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Tech Solutions"))
                    .andExpect(jsonPath("$.industry").value("Software"));
        }

        @Test
        @DisplayName("Should return 400 for invalid update data")
        void updateCompany_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            CompanyDto invalidUpdate = CompanyDto.builder()
                    .name("")
                    .industry("Industry")
                    .address("Address")
                    .email("invalid-email")
                    .build();

            mockMvc.perform(put(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUpdate)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/company/{id} - Delete Company")
    class DeleteCompanyTests {

        @Test
        @DisplayName("Should delete company for admin user")
        void deleteCompany_WithAdminAuth_ShouldDeleteCompany() throws Exception {
            mockMvc.perform(delete(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(COMPANY_BASE_URL + "/" + testCompany.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent company")
        void deleteCompany_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(COMPANY_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/company/search - Search Companies")
    class SearchCompanyTests {

        @Test
        @DisplayName("Should search companies by name for admin user")
        void searchCompany_ByName_ShouldReturnMatchingCompanies() throws Exception {
            CompanySearchRequest searchRequest = new CompanySearchRequest("Tech");

            mockMvc.perform(post(COMPANY_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("Tech Solutions Inc"));
        }

        @Test
        @DisplayName("Should search companies by industry")
        void searchCompany_ByIndustry_ShouldReturnMatchingCompanies() throws Exception {
            CompanySearchRequest searchRequest = new CompanySearchRequest("Finance");

            mockMvc.perform(post(COMPANY_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].industry").value("Finance"));
        }

        @Test
        @DisplayName("Should search companies by email domain")
        void searchCompany_ByEmail_ShouldReturnMatchingCompanies() throws Exception {
            CompanySearchRequest searchRequest = new CompanySearchRequest("techsolutions.com");

            mockMvc.perform(post(COMPANY_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should return empty results for non-matching query")
        void searchCompany_NoMatch_ShouldReturnEmpty() throws Exception {
            CompanySearchRequest searchRequest = new CompanySearchRequest("NonExistent");

            mockMvc.perform(post(COMPANY_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }
}
