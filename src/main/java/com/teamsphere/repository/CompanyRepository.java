package com.teamsphere.repository;

import com.teamsphere.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CompanyEntity.
 * Provides CRUD operations and query methods for companies.
 */
@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
