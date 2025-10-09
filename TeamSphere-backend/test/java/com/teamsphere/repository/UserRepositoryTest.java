package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void save_shouldPersistUser() {
        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void findById_shouldReturnUser() {
        // Given
        User persisted = entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByEmail_shouldReturnUser() {
        // Given
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findByEmail_whenNotExists_shouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Given
        entityManager.persist(user);
        
        User user2 = User.builder()
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("encodedPassword2")
                .role(Role.ROLE_ADMIN)
                .build();
        entityManager.persist(user2);
        entityManager.flush();

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    @Test
    void delete_shouldRemoveUser() {
        // Given
        User persisted = entityManager.persistAndFlush(user);

        // When
        userRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<User> found = userRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyUser() {
        // Given
        User persisted = entityManager.persistAndFlush(user);

        // When
        persisted.setFirstName("Johnny");
        persisted.setRole(Role.ROLE_ADMIN);
        User updated = userRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

