package com.teamsphere.dto.employee;

import jakarta.validation.constraints.NotEmpty;

public record EmployeeSearchRequest(@NotEmpty String query) {
}
