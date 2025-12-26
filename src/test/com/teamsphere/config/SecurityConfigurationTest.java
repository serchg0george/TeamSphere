package com.teamsphere.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SecurityConfiguration.
 * Tests CORS configuration and authentication manager creation.
 */
class SecurityConfigurationTest {

    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        // Create with null dependencies - we're only testing methods that don't need them
        securityConfiguration = new SecurityConfiguration(null, null);
    }

    @Test
    @DisplayName("corsConfigurer should return valid CorsConfigurationSource")
    void corsConfigurer_shouldReturnValidCorsConfigurationSource() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfiguration.corsConfigurer();

        // Then
        assertNotNull(corsConfigurationSource);
        assertInstanceOf(UrlBasedCorsConfigurationSource.class, corsConfigurationSource);
    }

    @Test
    @DisplayName("corsConfigurer should configure allowed origins patterns")
    void corsConfigurer_shouldConfigureAllowedOriginPatterns() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfiguration.corsConfigurer();

        // Then
        assertNotNull(corsConfigurationSource);
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        CorsConfiguration corsConfig = source.getCorsConfigurations().get("/**");
        assertNotNull(corsConfig);
        assertTrue(corsConfig.getAllowedOriginPatterns().contains("*"));
    }

    @Test
    @DisplayName("corsConfigurer should configure allowed methods")
    void corsConfigurer_shouldConfigureAllowedMethods() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfiguration.corsConfigurer();

        // Then
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        CorsConfiguration corsConfig = source.getCorsConfigurations().get("/**");
        assertNotNull(corsConfig);
        assertTrue(corsConfig.getAllowedMethods().contains("GET"));
        assertTrue(corsConfig.getAllowedMethods().contains("POST"));
        assertTrue(corsConfig.getAllowedMethods().contains("PUT"));
        assertTrue(corsConfig.getAllowedMethods().contains("DELETE"));
        assertTrue(corsConfig.getAllowedMethods().contains("OPTIONS"));
    }

    @Test
    @DisplayName("corsConfigurer should allow credentials")
    void corsConfigurer_shouldAllowCredentials() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfiguration.corsConfigurer();

        // Then
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        CorsConfiguration corsConfig = source.getCorsConfigurations().get("/**");
        assertNotNull(corsConfig);
        assertTrue(corsConfig.getAllowCredentials());
    }

    @Test
    @DisplayName("corsConfigurer should configure allowed headers")
    void corsConfigurer_shouldConfigureAllowedHeaders() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfiguration.corsConfigurer();

        // Then
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        CorsConfiguration corsConfig = source.getCorsConfigurations().get("/**");
        assertNotNull(corsConfig);
        assertTrue(corsConfig.getAllowedHeaders().contains("*"));
    }

    @Test
    @DisplayName("authenticationManager should return AuthenticationManager from configuration")
    void authenticationManager_shouldReturnAuthenticationManager() throws Exception {
        // Given
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        // When
        AuthenticationManager result = securityConfiguration.authenticationManager(authenticationConfiguration);

        // Then
        assertNotNull(result);
        assertEquals(expectedManager, result);
    }

    @Test
    @DisplayName("URL constants should be correctly defined")
    void urlConstants_shouldBeCorrectlyDefined() {
        assertEquals("/api/v1/company", SecurityConfiguration.COMPANY_URL);
        assertEquals("/api/v1/department", SecurityConfiguration.DEPARTMENT_URL);
        assertEquals("/api/v1/employee", SecurityConfiguration.EMPLOYEE_URL);
        assertEquals("/api/v1/position", SecurityConfiguration.POSITION_URL);
        assertEquals("/api/v1/project", SecurityConfiguration.PROJECT_URL);
        assertEquals("/api/v1/task", SecurityConfiguration.TASK_URL);
        assertEquals("/api/v1/search", SecurityConfiguration.SEARCH_URL);
        assertEquals("ADMIN", SecurityConfiguration.ROLE_ADMIN);
    }
}
