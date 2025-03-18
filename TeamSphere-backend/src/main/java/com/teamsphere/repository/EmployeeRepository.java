package com.teamsphere.repository;

import com.teamsphere.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    @EntityGraph(attributePaths = {"tasks", "projects", "department", "position"})
    @Query("SELECT e FROM EmployeeEntity e ORDER BY e.id DESC")
    List<EmployeeEntity> findAllWithRelations();

}
