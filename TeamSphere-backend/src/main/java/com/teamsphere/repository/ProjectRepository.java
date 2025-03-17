package com.teamsphere.repository;

import com.teamsphere.entity.ProjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT p FROM ProjectEntity p")
    List<ProjectEntity> findAllWithCompanies();
}
