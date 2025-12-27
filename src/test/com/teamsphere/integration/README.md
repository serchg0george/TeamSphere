# TeamSphere Integration Tests

## Overview

This directory contains comprehensive integration tests for the TeamSphere application. These tests verify the complete functionality of controllers, services, repositories, and security configuration with a real Spring context.

## Test Structure

```
integration/
├── BaseIntegrationTest.java          # Base class for all integration tests (H2)
├── PostgresIntegrationTest.java      # Base class for PostgreSQL Testcontainers tests
├── controller/
│   ├── AuthControllerIntegrationTest.java
│   ├── CompanyControllerIntegrationTest.java
│   ├── DepartmentControllerIntegrationTest.java
│   ├── EmployeeControllerIntegrationTest.java
│   ├── PositionControllerIntegrationTest.java
│   ├── ProjectControllerIntegrationTest.java
│   └── TaskControllerIntegrationTest.java
├── repository/
│   ├── RepositoryIntegrationTest.java
│   └── UserRepositoryIntegrationTest.java
└── security/
    └── SecurityIntegrationTest.java
```

## Running Tests

### Using Maven Wrapper (Recommended)

**Windows:**
```cmd
mvnw.cmd clean test
```

**Unix/Linux/macOS:**
```bash
./mvnw clean test
```

### Using IntelliJ IDEA

1. Right-click on the test class or package
2. Select "Run Tests"

Or use the Run Configuration:
1. Edit Configurations > Add New > JUnit
2. Set test scope (class, package, or all tests)
3. Click Run

### Running Specific Test Categories

**Controller Integration Tests:**
```cmd
mvnw.cmd test -Dtest="*ControllerIntegrationTest"
```

**Repository Integration Tests:**
```cmd
mvnw.cmd test -Dtest="*RepositoryIntegrationTest"
```

**Security Tests:**
```cmd
mvnw.cmd test -Dtest="SecurityIntegrationTest"
```

### Running with Testcontainers (PostgreSQL)

Requires Docker to be running:
```cmd
mvnw.cmd test -Dtest="*PostgresIT"
```

## Test Coverage Report

After running tests, view the JaCoCo coverage report:

```cmd
mvnw.cmd test jacoco:report
```

Report location: `target/site/jacoco/index.html`

## Test Categories

### Controller Integration Tests
- Full HTTP request/response testing
- Authentication and authorization verification
- Input validation testing
- CRUD operation testing
- Pagination and search functionality
- Error handling scenarios

### Repository Integration Tests
- JPA query verification
- Entity relationship testing
- Custom repository method testing
- Cascade operations
- Data integrity checks

### Security Integration Tests
- JWT token validation
- Role-based access control (RBAC)
- Public vs. protected endpoint verification
- CORS configuration
- Authentication flow testing

## Test Data Management

- Each test uses `@Transactional` for automatic rollback
- Test users (admin/regular) are created before each test
- H2 in-memory database for fast, isolated testing
- Optional PostgreSQL Testcontainers for production-like testing

## Configuration

### Test Application Properties

Located at: `src/test/resources/application.yml`

- H2 in-memory database
- JWT test secret key
- Debug logging enabled

### Dependencies Added

```xml
<!-- Testcontainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Writing New Integration Tests

1. Extend `BaseIntegrationTest` for H2 tests
2. Extend `PostgresIntegrationTest` for Testcontainers tests
3. Use `@DisplayName` for descriptive test names
4. Use `@Nested` classes for logical grouping
5. Follow the Given-When-Then pattern

Example:
```java
@DisplayName("MyController Integration Tests")
class MyControllerIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/v1/my-resource")
    class GetTests {
        
        @Test
        @DisplayName("Should return resource for authenticated user")
        void get_WithAuth_ShouldSucceed() throws Exception {
            mockMvc.perform(get("/api/v1/my-resource")
                    .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk());
        }
    }
}
```

