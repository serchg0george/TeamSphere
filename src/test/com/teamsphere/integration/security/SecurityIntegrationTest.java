package com.teamsphere.integration.security;

import com.teamsphere.integration.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for security configuration.
 * Tests authentication, authorization, and security filter chain behavior.
 */
@DisplayName("Security Integration Tests")
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("Public Endpoint Tests")
    class PublicEndpointTests {

        @Test
        @DisplayName("Swagger UI should be accessible without authentication")
        void swaggerUI_ShouldBeAccessibleWithoutAuth() throws Exception {
            mockMvc.perform(get("/swagger-ui.html"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("OpenAPI docs should be accessible without authentication")
        void openApiDocs_ShouldBeAccessibleWithoutAuth() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Auth login endpoint should be accessible without authentication")
        void login_ShouldBeAccessibleWithoutAuth() throws Exception {
            // Just check the endpoint is accessible (request will fail due to invalid credentials)
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType("application/json")
                            .content("{\"email\": \"test@test.com\", \"password\": \"test\"}"))
                    .andExpect(status().isUnauthorized()); // Unauthorized due to bad credentials, not 403
        }
    }

    @Nested
    @DisplayName("Protected Endpoint Tests - Unauthenticated")
    class UnauthenticatedAccessTests {

        @Test
        @DisplayName("Company endpoint should return 401 for unauthenticated requests")
        void companyEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/company"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Department endpoint should return 401 for unauthenticated requests")
        void departmentEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/department"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Employee endpoint should return 401 for unauthenticated requests")
        void employeeEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/employee"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Position endpoint should return 401 for unauthenticated requests")
        void positionEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/position"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Project endpoint should return 401 for unauthenticated requests")
        void projectEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/project"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Task endpoint should return 401 for unauthenticated requests")
        void taskEndpoint_ShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/v1/task"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control - GET Requests")
    class RoleBasedGetAccessTests {

        @Test
        @DisplayName("Regular user should access GET company endpoint")
        void companyGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/company")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Regular user should access GET department endpoint")
        void departmentGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/department")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Regular user should access GET employee endpoint")
        void employeeGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/employee")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Regular user should access GET position endpoint")
        void positionGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/position")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Regular user should access GET project endpoint")
        void projectGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/project")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Regular user should access GET task endpoint")
        void taskGet_RegularUser_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/task")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control - POST Requests (Admin Only)")
    class RoleBasedPostAccessTests {

        @Test
        @DisplayName("Regular user should NOT create company")
        void companyPost_RegularUser_ShouldBeForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/company")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType("application/json")
                            .content("{\"name\": \"Test\", \"industry\": \"Tech\", \"address\": \"123\", \"email\": \"t@t.com\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Admin user should create company")
        void companyPost_AdminUser_ShouldSucceed() throws Exception {
            mockMvc.perform(post("/api/v1/company")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType("application/json")
                            .content("{\"name\": \"Test Company\", \"industry\": \"Tech\", \"address\": \"123 Street\", \"email\": \"test@company.com\"}"))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Regular user should NOT create department")
        void departmentPost_RegularUser_ShouldBeForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/department")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType("application/json")
                            .content("{\"departmentName\": \"Test\", \"description\": \"Test Dept\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Admin user should create department")
        void departmentPost_AdminUser_ShouldSucceed() throws Exception {
            mockMvc.perform(post("/api/v1/department")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType("application/json")
                            .content("{\"departmentName\": \"Test Department\", \"description\": \"Test Description\"}"))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control - Search Endpoints (Admin Only)")
    class RoleBasedSearchAccessTests {

        @Test
        @DisplayName("Admin user should access company search")
        void companySearch_AdminUser_ShouldSucceed() throws Exception {
            mockMvc.perform(post("/api/v1/company/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType("application/json")
                            .content("{\"query\": \"test\"}"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("JWT Token Validation Tests")
    class JwtTokenTests {

        @Test
        @DisplayName("Request without Bearer prefix should return 401")
        void noBearerPrefix_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/company")
                            .header(HttpHeaders.AUTHORIZATION, adminToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Valid token should allow access")
        void validToken_ShouldAllowAccess() throws Exception {
            mockMvc.perform(get("/api/v1/company")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("CORS Tests")
    class CorsTests {

        @Test
        @DisplayName("OPTIONS request should succeed for CORS preflight")
        void options_ShouldSucceedForCors() throws Exception {
            mockMvc.perform(options("/api/v1/company")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk());
        }
    }
}

