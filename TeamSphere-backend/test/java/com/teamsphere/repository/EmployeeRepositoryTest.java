package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.DepartmentEntity;
import com.teamsphere.entity.EmployeeEntity;
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
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity employee;
    private DepartmentEntity department;
    private PositionEntity position;

    @BeforeEach
    void setUp() {
        department = new DepartmentEntity();
        department.setCreatedAt(java.time.LocalDateTime.now());
        department.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        department.setDepartmentName("Engineering");
        department.setDescription("Engineering Department");
        entityManager.persist(department);

        position = new PositionEntity();
        position.setCreatedAt(java.time.LocalDateTime.now());
        position.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        position.setPositionName("Senior Developer");
        position.setYearsOfExperience(5);
        entityManager.persist(position);

        employee = new EmployeeEntity();
        employee.setCreatedAt(java.time.LocalDateTime.now());
        employee.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setAddress("123 Main St");
        employee.setPin("1234567890");
        employee.setDepartment(department);
        employee.setPosition(position);
        
        entityManager.flush();
    }

    @Test
    void save_shouldPersistEmployee() {
        // When
        EmployeeEntity saved = employeeRepository.save(employee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findById_shouldReturnEmployee() {
        // Given
        EmployeeEntity persisted = entityManager.persistAndFlush(employee);

        // When
        Optional<EmployeeEntity> found = employeeRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findAllWithRelations_shouldReturnEmployeesWithRelations() {
        // Given
        entityManager.persist(employee);
        
        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setCreatedAt(java.time.LocalDateTime.now());
        employee2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setEmail("jane.smith@example.com");
        employee2.setAddress("456 Elm St");
        employee2.setPin("0987654321");
        employee2.setDepartment(department);
        employee2.setPosition(position);
        entityManager.persist(employee2);
        entityManager.flush();

        // When
        List<EmployeeEntity> employees = employeeRepository.findAllWithRelations();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getDepartment()).isNotNull();
        assertThat(employees.get(0).getPosition()).isNotNull();
    }

    @Test
    void findAll_shouldReturnAllEmployees() {
        // Given
        entityManager.persist(employee);
        
        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setCreatedAt(java.time.LocalDateTime.now());
        employee2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setEmail("jane.smith@example.com");
        employee2.setAddress("456 Elm St");
        employee2.setPin("0987654321");
        employee2.setDepartment(department);
        employee2.setPosition(position);
        entityManager.persist(employee2);
        entityManager.flush();

        // When
        List<EmployeeEntity> employees = employeeRepository.findAll();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(EmployeeEntity::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void delete_shouldRemoveEmployee() {
        // Given
        EmployeeEntity persisted = entityManager.persistAndFlush(employee);

        // When
        employeeRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<EmployeeEntity> found = employeeRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyEmployee() {
        // Given
        EmployeeEntity persisted = entityManager.persistAndFlush(employee);

        // When
        persisted.setFirstName("Johnny");
        persisted.setEmail("johnny.doe@example.com");
        EmployeeEntity updated = employeeRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getEmail()).isEqualTo("johnny.doe@example.com");
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

