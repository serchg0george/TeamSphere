package com.teamsphere.repository;
import java.time.LocalDateTime;
import com.teamsphere.entity.CompanyEntity;
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
class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        company = new CompanyEntity();
        company.setCreatedAt(java.time.LocalDateTime.now());
        company.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        company.setName("Tech Corp");
        company.setIndustry("Technology");
        company.setAddress("123 Tech Street");
        company.setEmail("contact@techcorp.com");
    }

    @Test
    void save_shouldPersistCompany() {
        // When
        CompanyEntity saved = companyRepository.save(company);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Tech Corp");
        assertThat(saved.getIndustry()).isEqualTo("Technology");
    }

    @Test
    void findById_shouldReturnCompany() {
        // Given
        CompanyEntity persisted = entityManager.persistAndFlush(company);

        // When
        Optional<CompanyEntity> found = companyRepository.findById(persisted.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tech Corp");
    }

    @Test
    void findAll_shouldReturnAllCompanies() {
        // Given
        entityManager.persist(company);
        
        CompanyEntity company2 = new CompanyEntity();
        company2.setCreatedAt(java.time.LocalDateTime.now());
        company2.setUpdatedAt(java.time.LocalDateTime.now());        LocalDateTime now = LocalDateTime.now();        company2.setName("Finance Inc");
        company2.setIndustry("Finance");
        company2.setAddress("456 Finance Ave");
        company2.setEmail("info@financeinc.com");
        entityManager.persist(company2);
        entityManager.flush();

        // When
        List<CompanyEntity> companies = companyRepository.findAll();

        // Then
        assertThat(companies).hasSize(2);
        assertThat(companies).extracting(CompanyEntity::getName)
                .containsExactlyInAnyOrder("Tech Corp", "Finance Inc");
    }

    @Test
    void delete_shouldRemoveCompany() {
        // Given
        CompanyEntity persisted = entityManager.persistAndFlush(company);

        // When
        companyRepository.delete(persisted);
        entityManager.flush();

        // Then
        Optional<CompanyEntity> found = companyRepository.findById(persisted.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyCompany() {
        // Given
        CompanyEntity persisted = entityManager.persistAndFlush(company);

        // When
        persisted.setName("Updated Corp");
        CompanyEntity updated = companyRepository.save(persisted);
        entityManager.flush();

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Corp");
        assertThat(updated.getId()).isEqualTo(persisted.getId());
    }
}

