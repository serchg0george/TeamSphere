package com.teamsphere.repository;

import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT COALESCE(MAX(CAST(t.taskNumber AS biginteger)), 0) FROM TaskEntity t WHERE t.taskType = :type")
    Optional<Long> findLastTaskByTaskType(@Param("type") TaskType type);

    @Query("SELECT t FROM TaskEntity t ORDER BY " +
            "CASE t.taskStatus " +
            "WHEN 'ACTIVE' THEN 1 " +
            "WHEN 'PENDING' THEN 2 " +
            "WHEN 'FINISHED' THEN 3 " +
            "ELSE 4 END, " +
            "t.updatedAt DESC")
    Page<TaskEntity> findAllSorted(Pageable pageable);

}
