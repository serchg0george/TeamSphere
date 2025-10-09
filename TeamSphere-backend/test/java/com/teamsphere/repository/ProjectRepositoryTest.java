package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.CompanyEntity;
import com.teamsphere.entity.ProjectEntity;
import com.teamsphere.entity.enums.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private ProjectEntity project;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        company = new CompanyEntity();
        company.setCreatedAt(java.time.LocalDateTime.now());
        company.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        company.setName("Tech Corp");
        company.setIndustry("Technology");
        company.setAddress("123 Tech Street");
        company.setEmail("contact@techcorp.com");
        entityManager.persist(company);

        project = new ProjectEntity();
        project.setCreatedAt(java.time.LocalDateTime.now());
        project.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        project.setName("Project Alpha");
        project.setDescription("First project");
        project.setStartDate(LocalDate.of(2024, 1, 1));
        project.setFinishDate(LocalDate.of(2024, 12, 31));
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setCompany(company);
        
        entityManager.flush();
    }

    @Test
    void save_shouldPersistProject() {
        // When
        ProjectEntity saved = projectRepository.save(project);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Project Alpha");
        assertThat(saved.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }

    @Test
    void findById_shouldReturnProject() {
        // Given
        ProjectEntity persisted = entityManager.persistAndFlush(project);

        // When
        Optional<ProjectEntity> found = projectRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Project Alpha");
        assertThat(found.get().getCompany()).isNotNull();
    }

    @Test
    void findAllWithCompanies_shouldReturnProjectsWithCompanies() {
        // Given
        entityManager.persist(project);
        
        ProjectEntity project2 = new ProjectEntity();
        project2.setCreatedAt(java.time.LocalDateTime.now());
        project2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        project2.setName("Project Beta");
        project2.setDescription("Second project");
        project2.setStartDate(LocalDate.of(2024, 6, 1));
        project2.setStatus(ProjectStatus.IN_PROGRESS);
        project2.setCompany(company);
        entityManager.persist(project2);
        entityManager.flush();

        // When
        List<ProjectEntity> projects = projectRepository.findAllWithCompanies();

        // Then
        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).getCompany()).isNotNull();
        assertThat(projects.get(0).getCompany().getName()).isEqualTo("Tech Corp");
    }

    @Test
    void findAll_shouldReturnAllProjects() {
        // Given
        entityManager.persist(project);
        
        ProjectEntity project2 = new ProjectEntity();
        project2.setCreatedAt(java.time.LocalDateTime.now());
        project2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        project2.setName("Project Beta");
        project2.setDescription("Second project");
        project2.setStartDate(LocalDate.of(2024, 6, 1));
        project2.setStatus(ProjectStatus.IN_PROGRESS);
        project2.setCompany(company);
        entityManager.persist(project2);
        entityManager.flush();

        // When
        List<ProjectEntity> projects = projectRepository.findAll();

        // Then
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting(ProjectEntity::getName)
                .containsExactlyInAnyOrder("Project Alpha", "Project Beta");
    }

    @Test
    void delete_shouldRemoveProject() {
        // Given
        ProjectEntity persisted = entityManager.persistAndFlush(project);

        // When
        projectRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<ProjectEntity> found = projectRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyProject() {
        // Given
        ProjectEntity persisted = entityManager.persistAndFlush(project);

        // When
        persisted.setName("Updated Project Alpha");
        persisted.setStatus(ProjectStatus.FINISHED);
        ProjectEntity updated = projectRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Project Alpha");
        assertThat(updated.getStatus()).isEqualTo(ProjectStatus.FINISHED);
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

