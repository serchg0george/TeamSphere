package com.teamsphere.entity.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity.
 * Tests UserDetails interface implementation and entity behavior.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
        user.setId(1L);
    }

    @Test
    @DisplayName("getAuthorities should return authority based on user role")
    void getAuthorities_shouldReturnAuthorityBasedOnRole() {
        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("getAuthorities should return ROLE_ADMIN for admin users")
    void getAuthorities_shouldReturnAdminAuthority() {
        // Given
        User adminUser = User.builder()
                .email("admin@test.com")
                .password("encodedPassword")
                .role(Role.ROLE_ADMIN)
                .build();
        adminUser.setId(2L);

        // When
        Collection<? extends GrantedAuthority> authorities = adminUser.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("getUsername should return email")
    void getUsername_shouldReturnEmail() {
        // When
        String username = user.getUsername();

        // Then
        assertEquals("john.doe@test.com", username);
    }

    @Test
    @DisplayName("getPassword should return password")
    void getPassword_shouldReturnPassword() {
        // When
        String password = user.getPassword();

        // Then
        assertEquals("encodedPassword", password);
    }

    @Test
    @DisplayName("User builder should create user with all fields")
    void builder_shouldCreateUserWithAllFields() {
        // Given
        User builtUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@test.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
        builtUser.setId(1L);

        // Then
        assertEquals(1L, builtUser.getId());
        assertEquals("Jane", builtUser.getFirstName());
        assertEquals("Smith", builtUser.getLastName());
        assertEquals("jane.smith@test.com", builtUser.getEmail());
        assertEquals("password123", builtUser.getPassword());
        assertEquals(Role.ROLE_USER, builtUser.getRole());
    }

    @Test
    @DisplayName("No-args constructor should create empty user")
    void noArgsConstructor_shouldCreateEmptyUser() {
        // When
        User emptyUser = new User();

        // Then
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getFirstName());
        assertNull(emptyUser.getLastName());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getRole());
    }

    @Test
    @DisplayName("All-args constructor should create user with all fields")
    void allArgsConstructor_shouldCreateUserWithAllFields() {
        // When
        User constructedUser = new User("Alice", "Johnson", "alice@test.com", "pass", Role.ROLE_ADMIN);

        // Then
        assertEquals("Alice", constructedUser.getFirstName());
        assertEquals("Johnson", constructedUser.getLastName());
        assertEquals("alice@test.com", constructedUser.getEmail());
        assertEquals("pass", constructedUser.getPassword());
        assertEquals(Role.ROLE_ADMIN, constructedUser.getRole());
    }

    @Test
    @DisplayName("Setters should update user fields")
    void setters_shouldUpdateFields() {
        // Given
        User mutableUser = new User();

        // When
        mutableUser.setId(5L);
        mutableUser.setFirstName("Updated");
        mutableUser.setLastName("User");
        mutableUser.setEmail("updated@test.com");
        mutableUser.setPassword("newpass");
        mutableUser.setRole(Role.ROLE_ADMIN);

        // Then
        assertEquals(5L, mutableUser.getId());
        assertEquals("Updated", mutableUser.getFirstName());
        assertEquals("User", mutableUser.getLastName());
        assertEquals("updated@test.com", mutableUser.getEmail());
        assertEquals("newpass", mutableUser.getPassword());
        assertEquals(Role.ROLE_ADMIN, mutableUser.getRole());
    }
}

