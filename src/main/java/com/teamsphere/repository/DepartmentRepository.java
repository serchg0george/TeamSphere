package com.teamsphere.repository;

import com.teamsphere.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for DepartmentEntity.
 * Provides CRUD operations and query methods for departments.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
}
