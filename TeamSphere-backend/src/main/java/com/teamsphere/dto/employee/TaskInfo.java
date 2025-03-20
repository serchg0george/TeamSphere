package com.teamsphere.dto.employee;

public record TaskInfo(Long id, String taskNumber, String taskStatus, String taskDescription, Integer timeSpentMinutes) {
}
