package com.teamsphere.repository;

import com.teamsphere.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for EmployeeEntity.
 * Provides CRUD operations and query methods for employees.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    /**
     * Finds all employees with their related entities (tasks, projects, department, position).
     * Uses entity graph to avoid N+1 query problem.
     *
     * @return list of employees with all relations loaded, ordered by ID descending
     */
    @EntityGraph(attributePaths = {"tasks", "projects", "department", "position"})
    @Query("SELECT e FROM EmployeeEntity e ORDER BY e.id DESC")
    List<EmployeeEntity> findAllWithRelations();

}
