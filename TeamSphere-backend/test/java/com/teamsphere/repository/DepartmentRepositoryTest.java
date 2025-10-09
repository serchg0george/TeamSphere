package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.DepartmentEntity;
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
class DepartmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentEntity department;

    @BeforeEach
    void setUp() {
        department = new DepartmentEntity();
        department.setCreatedAt(java.time.LocalDateTime.now());
        department.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        department.setDepartmentName("Engineering");
        department.setDescription("Engineering Department");
    }

    @Test
    void save_shouldPersistDepartment() {
        // When
        DepartmentEntity saved = departmentRepository.save(department);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDepartmentName()).isEqualTo("Engineering");
        assertThat(saved.getDescription()).isEqualTo("Engineering Department");
    }

    @Test
    void findById_shouldReturnDepartment() {
        // Given
        DepartmentEntity persisted = entityManager.persistAndFlush(department);

        // When
        Optional<DepartmentEntity> found = departmentRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDepartmentName()).isEqualTo("Engineering");
    }

    @Test
    void findAll_shouldReturnAllDepartments() {
        // Given
        entityManager.persist(department);
        
        DepartmentEntity department2 = new DepartmentEntity();
        department2.setCreatedAt(java.time.LocalDateTime.now());
        department2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        department2.setDepartmentName("HR");
        department2.setDescription("Human Resources Department");
        entityManager.persist(department2);
        entityManager.flush();

        // When
        List<DepartmentEntity> departments = departmentRepository.findAll();

        // Then
        assertThat(departments).hasSize(2);
        assertThat(departments).extracting(DepartmentEntity::getDepartmentName)
                .containsExactlyInAnyOrder("Engineering", "HR");
    }

    @Test
    void delete_shouldRemoveDepartment() {
        // Given
        DepartmentEntity persisted = entityManager.persistAndFlush(department);

        // When
        departmentRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<DepartmentEntity> found = departmentRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyDepartment() {
        // Given
        DepartmentEntity persisted = entityManager.persistAndFlush(department);

        // When
        persisted.setDepartmentName("Updated Engineering");
        DepartmentEntity updated = departmentRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getDepartmentName()).isEqualTo("Updated Engineering");
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

