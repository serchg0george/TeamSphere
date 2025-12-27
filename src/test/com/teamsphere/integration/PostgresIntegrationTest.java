package com.teamsphere.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests using PostgreSQL Testcontainers.
 * Provides a shared PostgreSQL container for tests that require PostgreSQL-specific features.
 * 
 * <p>Usage: Extend this class instead of BaseIntegrationTest when you need to test
 * with a real PostgreSQL database rather than H2.</p>
 * 
 * <p>Note: Requires Docker to be running on the test environment.</p>
 * 
 * <p>To run tests with Testcontainers:
 * <ul>
 *   <li>Windows: mvnw.cmd test -Dtest=*PostgresIT</li>
 *   <li>Unix/Linux: ./mvnw test -Dtest=*PostgresIT</li>
 *   <li>IntelliJ IDEA: Right-click test class > Run</li>
 * </ul>
 * </p>
 */
@Testcontainers
public abstract class PostgresIntegrationTest extends BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("teamsphere_test")
            .withUsername("test")
            .withPassword("test");

    /**
     * Configures Spring datasource properties to use the Testcontainers PostgreSQL instance.
     *
     * @param registry the dynamic property registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}

