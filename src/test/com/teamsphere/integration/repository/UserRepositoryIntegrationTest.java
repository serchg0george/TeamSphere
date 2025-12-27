package com.teamsphere.integration.repository;

import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import com.teamsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository.
 * Tests user authentication-related repository operations.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@teamsphere.com")
                .password("hashedPassword123")
                .role(Role.ROLE_ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        regularUser = User.builder()
                .firstName("Regular")
                .lastName("User")
                .email("user@teamsphere.com")
                .password("hashedPassword456")
                .role(Role.ROLE_USER)
                .build();
        regularUser = userRepository.save(regularUser);
    }

    @Nested
    @DisplayName("findByEmail Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email")
        void findByEmail_WithExistingEmail_ShouldReturnUser() {
            Optional<User> found = userRepository.findByEmail("admin@teamsphere.com");

            assertThat(found).isPresent();
            assertThat(found.get().getFirstName()).isEqualTo("Admin");
            assertThat(found.get().getRole()).isEqualTo(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("Should return empty for non-existent email")
        void findByEmail_WithNonExistentEmail_ShouldReturnEmpty() {
            Optional<User> found = userRepository.findByEmail("nonexistent@teamsphere.com");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should be case sensitive for email lookup")
        void findByEmail_ShouldBeCaseSensitive() {
            Optional<User> found = userRepository.findByEmail("ADMIN@teamsphere.com");

            // Email lookup should be case-sensitive by default in H2
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find regular user by email")
        void findByEmail_RegularUser_ShouldReturnUser() {
            Optional<User> found = userRepository.findByEmail("user@teamsphere.com");

            assertThat(found).isPresent();
            assertThat(found.get().getRole()).isEqualTo(Role.ROLE_USER);
        }
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should save user with all fields")
        void saveUser_WithAllFields_ShouldPersist() {
            User newUser = User.builder()
                    .firstName("New")
                    .lastName("User")
                    .email("new.user@teamsphere.com")
                    .password("securePassword789")
                    .role(Role.ROLE_USER)
                    .build();

            User saved = userRepository.save(newUser);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getEmail()).isEqualTo("new.user@teamsphere.com");
        }

        @Test
        @DisplayName("Should update user")
        void updateUser_ShouldPersistChanges() {
            adminUser.setFirstName("UpdatedAdmin");
            adminUser.setLastName("UpdatedUser");

            User updated = userRepository.save(adminUser);

            assertThat(updated.getFirstName()).isEqualTo("UpdatedAdmin");
            assertThat(updated.getLastName()).isEqualTo("UpdatedUser");
        }

        @Test
        @DisplayName("Should delete user")
        void deleteUser_ShouldRemoveFromDatabase() {
            Long userId = regularUser.getId();

            userRepository.delete(regularUser);

            Optional<User> found = userRepository.findById(userId);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find user by ID")
        void findById_ShouldReturnUser() {
            Optional<User> found = userRepository.findById(adminUser.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("admin@teamsphere.com");
        }

        @Test
        @DisplayName("Should count all users")
        void count_ShouldReturnCorrectCount() {
            long count = userRepository.count();

            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Role Tests")
    class RoleTests {

        @Test
        @DisplayName("Should save user with ADMIN role")
        void saveUser_WithAdminRole_ShouldPersist() {
            User admin = User.builder()
                    .firstName("Another")
                    .lastName("Admin")
                    .email("another.admin@teamsphere.com")
                    .password("adminPass")
                    .role(Role.ROLE_ADMIN)
                    .build();

            User saved = userRepository.save(admin);

            assertThat(saved.getRole()).isEqualTo(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("Should save user with USER role")
        void saveUser_WithUserRole_ShouldPersist() {
            User user = User.builder()
                    .firstName("Another")
                    .lastName("User")
                    .email("another.user@teamsphere.com")
                    .password("userPass")
                    .role(Role.ROLE_USER)
                    .build();

            User saved = userRepository.save(user);

            assertThat(saved.getRole()).isEqualTo(Role.ROLE_USER);
        }

        @Test
        @DisplayName("Should update user role")
        void updateUserRole_ShouldPersistChanges() {
            regularUser.setRole(Role.ROLE_ADMIN);

            User updated = userRepository.save(regularUser);

            assertThat(updated.getRole()).isEqualTo(Role.ROLE_ADMIN);
        }
    }

    @Nested
    @DisplayName("UserDetails Interface Tests")
    class UserDetailsTests {

        @Test
        @DisplayName("User should return email as username")
        void getUsername_ShouldReturnEmail() {
            assertThat(adminUser.getUsername()).isEqualTo("admin@teamsphere.com");
        }

        @Test
        @DisplayName("User should return password")
        void getPassword_ShouldReturnPassword() {
            assertThat(adminUser.getPassword()).isEqualTo("hashedPassword123");
        }

        @Test
        @DisplayName("User should return authorities based on role")
        void getAuthorities_ShouldReturnRoleBasedAuthorities() {
            assertThat(adminUser.getAuthorities()).hasSize(1);
            assertThat(adminUser.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("Regular user should have USER authority")
        void getAuthorities_RegularUser_ShouldReturnUserAuthority() {
            assertThat(regularUser.getAuthorities()).hasSize(1);
            assertThat(regularUser.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_USER");
        }
    }
}

