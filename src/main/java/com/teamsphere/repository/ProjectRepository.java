package com.teamsphere.repository;

import com.teamsphere.entity.ProjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProjectEntity.
 * Provides CRUD operations and query methods for projects.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /**
     * Finds all projects with their associated company.
     * Uses entity graph to avoid N+1 query problem.
     *
     * @return list of projects with company loaded, ordered by ID descending
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT p FROM ProjectEntity p ORDER BY p.id DESC")
    List<ProjectEntity> findAllWithCompanies();
}
