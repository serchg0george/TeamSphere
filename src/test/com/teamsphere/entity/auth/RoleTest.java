package com.teamsphere.entity.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Role enum.
 */
class RoleTest {

    @Test
    @DisplayName("Role enum should have ROLE_ADMIN value")
    void roleEnum_shouldHaveRoleAdmin() {
        Role adminRole = Role.ROLE_ADMIN;
        assertNotNull(adminRole);
        assertEquals("ROLE_ADMIN", adminRole.name());
    }

    @Test
    @DisplayName("Role enum should have ROLE_USER value")
    void roleEnum_shouldHaveRoleUser() {
        Role userRole = Role.ROLE_USER;
        assertNotNull(userRole);
        assertEquals("ROLE_USER", userRole.name());
    }

    @Test
    @DisplayName("Role enum should have exactly 2 values")
    void roleEnum_shouldHaveTwoValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
    }

    @Test
    @DisplayName("Role valueOf should return correct enum")
    void roleValueOf_shouldReturnCorrectEnum() {
        assertEquals(Role.ROLE_ADMIN, Role.valueOf("ROLE_ADMIN"));
        assertEquals(Role.ROLE_USER, Role.valueOf("ROLE_USER"));
    }

    @Test
    @DisplayName("Role valueOf should throw exception for invalid value")
    void roleValueOf_shouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALID_ROLE"));
    }
}

