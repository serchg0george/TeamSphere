package com.teamsphere.repository;

import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT COALESCE(MAX(CAST(t.taskNumber AS biginteger)), 0) FROM TaskEntity t WHERE t.taskType = :type")
    Optional<Long> findLastTaskByTaskType(@Param("type") TaskType type);

}
