package com.teamsphere.dto.employee;

public record TaskInfo(Long id,
                       String taskNumber,
                       String taskStatus,
                       String taskType,
                       String taskPriority,
                       String taskDescription,
                       Integer timeSpentMinutes) {
}
