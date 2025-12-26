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

/**
 * Repository interface for TaskEntity.
 * Provides CRUD operations and query methods for tasks.
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    /**
     * Finds the last task number for a specific task type.
     * Used for generating sequential task numbers.
     *
     * @param type the task type to search for
     * @return the maximum task number for the given type, or 0 if none exist
     */
    @Query("SELECT COALESCE(MAX(CAST(t.taskNumber AS biginteger)), 0) FROM TaskEntity t WHERE t.taskType = :type")
    Optional<Long> findLastTaskByTaskType(@Param("type") TaskType type);

    /**
     * Finds all tasks sorted by status priority and update time.
     * Status priority: ACTIVE (1), PENDING (2), FINISHED (3), others (4).
     *
     * @param pageable pagination information
     * @return page of tasks sorted by status and update time
     */
    @Query("SELECT t FROM TaskEntity t ORDER BY " +
            "CASE t.taskStatus " +
            "WHEN 'ACTIVE' THEN 1 " +
            "WHEN 'PENDING' THEN 2 " +
            "WHEN 'FINISHED' THEN 3 " +
            "ELSE 4 END, " +
            "t.updatedAt DESC")
    Page<TaskEntity> findAllSorted(Pageable pageable);

}
