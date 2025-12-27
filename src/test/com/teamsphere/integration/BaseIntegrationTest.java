package com.teamsphere.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtService;
import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import com.teamsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests.
 * Provides common setup and utilities for testing with full Spring context.
 * Uses H2 in-memory database for isolated, fast testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User adminUser;
    protected User regularUser;
    protected String adminToken;
    protected String userToken;

    protected static final String ADMIN_EMAIL = "admin@teamsphere.com";
    protected static final String USER_EMAIL = "user@teamsphere.com";
    protected static final String TEST_PASSWORD = "password123";

    /**
     * Sets up test users and generates JWT tokens before each test.
     * Creates both admin and regular user for testing role-based access.
     */
    @BeforeEach
    void setUpBaseTest() {
        // Clear existing test users
        userRepository.deleteAll();

        // Create admin user
        adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .role(Role.ROLE_ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        // Create regular user
        regularUser = User.builder()
                .firstName("Regular")
                .lastName("User")
                .email(USER_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .role(Role.ROLE_USER)
                .build();
        regularUser = userRepository.save(regularUser);

        // Generate tokens
        adminToken = jwtService.generateToken(adminUser);
        userToken = jwtService.generateToken(regularUser);
    }

    /**
     * Creates authorization header value with Bearer token.
     *
     * @param token the JWT token
     * @return Bearer token string for Authorization header
     */
    protected String bearerToken(String token) {
        return "Bearer " + token;
    }
}

