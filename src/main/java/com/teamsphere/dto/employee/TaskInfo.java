package com.teamsphere.dto.employee;

/**
 * Brief task information for employee display.
 *
 * @param id               task ID
 * @param taskNumber       task number
 * @param taskStatus       task status
 * @param taskType         task type
 * @param taskPriority     task priority
 * @param taskDescription  task description
 * @param timeSpentMinutes time spent on task in minutes
 */
public record TaskInfo(Long id,
                       String taskNumber,
                       String taskStatus,
                       String taskType,
                       String taskPriority,
                       String taskDescription,
                       Integer timeSpentMinutes) {
}
