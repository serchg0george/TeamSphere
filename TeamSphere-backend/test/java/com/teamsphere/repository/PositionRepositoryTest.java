package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.PositionEntity;
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
class PositionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PositionRepository positionRepository;

    private PositionEntity position;

    @BeforeEach
    void setUp() {
        position = new PositionEntity();
        position.setCreatedAt(java.time.LocalDateTime.now());
        position.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        position.setPositionName("Senior Developer");
        position.setYearsOfExperience(5);
    }

    @Test
    void save_shouldPersistPosition() {
        // When
        PositionEntity saved = positionRepository.save(position);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPositionName()).isEqualTo("Senior Developer");
        assertThat(saved.getYearsOfExperience()).isEqualTo(5);
    }

    @Test
    void findById_shouldReturnPosition() {
        // Given
        PositionEntity persisted = entityManager.persistAndFlush(position);

        // When
        Optional<PositionEntity> found = positionRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPositionName()).isEqualTo("Senior Developer");
        assertThat(found.get().getYearsOfExperience()).isEqualTo(5);
    }

    @Test
    void findAll_shouldReturnAllPositions() {
        // Given
        entityManager.persist(position);
        
        PositionEntity position2 = new PositionEntity();
        position2.setCreatedAt(java.time.LocalDateTime.now());
        position2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        position2.setPositionName("Junior Developer");
        position2.setYearsOfExperience(1);
        entityManager.persist(position2);
        entityManager.flush();

        // When
        List<PositionEntity> positions = positionRepository.findAll();

        // Then
        assertThat(positions).hasSize(2);
        assertThat(positions).extracting(PositionEntity::getPositionName)
                .containsExactlyInAnyOrder("Senior Developer", "Junior Developer");
    }

    @Test
    void delete_shouldRemovePosition() {
        // Given
        PositionEntity persisted = entityManager.persistAndFlush(position);

        // When
        positionRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<PositionEntity> found = positionRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyPosition() {
        // Given
        PositionEntity persisted = entityManager.persistAndFlush(position);

        // When
        persisted.setPositionName("Lead Developer");
        persisted.setYearsOfExperience(7);
        PositionEntity updated = positionRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getPositionName()).isEqualTo("Lead Developer");
        assertThat(updated.getYearsOfExperience()).isEqualTo(7);
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

