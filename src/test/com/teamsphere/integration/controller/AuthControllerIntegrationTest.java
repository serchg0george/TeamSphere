package com.teamsphere.integration.controller;

import com.teamsphere.auth.AuthenticationResponse;
import com.teamsphere.dto.auth.AuthenticationRequestDto;
import com.teamsphere.dto.auth.RegisterRequestDto;
import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import com.teamsphere.integration.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 * Tests user registration and authentication endpoints with full security context.
 */
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    private static final String AUTH_BASE_URL = "/api/v1/auth";
    private static final String REGISTER_URL = AUTH_BASE_URL + "/register";
    private static final String LOGIN_URL = AUTH_BASE_URL + "/login";

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Should register new user successfully and return JWT token")
        void register_WithValidData_ShouldReturnToken() throws Exception {
            // Given
            RegisterRequestDto request = new RegisterRequestDto(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "securePassword123"
            );

            // When & Then
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Should return error when registering with invalid email format")
        void register_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
            // Given
            RegisterRequestDto request = new RegisterRequestDto(
                    "John",
                    "Doe",
                    "invalid-email",
                    "password123"
            );

            // When & Then
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create user with ROLE_USER by default")
        void register_ShouldCreateUserWithUserRole() throws Exception {
            // Given
            String newUserEmail = "newuser@example.com";
            RegisterRequestDto request = new RegisterRequestDto(
                    "New",
                    "User",
                    newUserEmail,
                    "password123"
            );

            // When
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Then - verify user was created with correct role
            User createdUser = userRepository.findByEmail(newUserEmail).orElseThrow();
            assert createdUser.getRole() == Role.ROLE_USER;
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should authenticate user with valid credentials and return JWT token")
        void login_WithValidCredentials_ShouldReturnToken() throws Exception {
            // Given - admin user already exists from BaseIntegrationTest setup
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    ADMIN_EMAIL,
                    TEST_PASSWORD
            );

            // When & Then
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Should authenticate regular user with valid credentials")
        void login_RegularUser_ShouldReturnToken() throws Exception {
            // Given - regular user already exists from BaseIntegrationTest setup
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    USER_EMAIL,
                    TEST_PASSWORD
            );

            // When & Then
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid password")
        void login_WithInvalidPassword_ShouldReturnUnauthorized() throws Exception {
            // Given
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    ADMIN_EMAIL,
                    "wrongPassword"
            );

            // When & Then
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for non-existent user")
        void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
            // Given
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    "nonexistent@example.com",
                    "password123"
            );

            // When & Then
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return bad request for invalid email format")
        void login_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
            // Given
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    "not-an-email",
                    "password123"
            );

            // When & Then
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return valid JWT token that can be decoded")
        void login_ShouldReturnValidJwtToken() throws Exception {
            // Given
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    ADMIN_EMAIL,
                    TEST_PASSWORD
            );

            // When
            String responseContent = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then
            AuthenticationResponse response = objectMapper.readValue(responseContent, AuthenticationResponse.class);
            String token = response.getToken();

            // Verify token is valid by extracting username
            String extractedUsername = jwtService.extractUsername(token);
            assert extractedUsername.equals(ADMIN_EMAIL);
        }
    }

    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @DisplayName("Register endpoint should be accessible without authentication")
        void register_ShouldBeAccessibleWithoutAuth() throws Exception {
            // Given
            RegisterRequestDto request = new RegisterRequestDto(
                    "Test",
                    "User",
                    "test.security@example.com",
                    "password123"
            );

            // When & Then - no auth token required
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Login endpoint should be accessible without authentication")
        void login_ShouldBeAccessibleWithoutAuth() throws Exception {
            // Given
            AuthenticationRequestDto request = new AuthenticationRequestDto(
                    ADMIN_EMAIL,
                    TEST_PASSWORD
            );

            // When & Then - no auth token required
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
}

