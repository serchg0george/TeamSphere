package com.teamsphere.entity.enums;

/**
 * Enumeration of task statuses.
 * Represents the current state of a task.
 */
public enum TaskStatus {
    /**
     * Task is pending and not yet started.
     */
    PENDING,
    
    /**
     * Task is currently being worked on.
     */
    ACTIVE,
    
    /**
     * Task has been completed.
     */
    FINISHED
}