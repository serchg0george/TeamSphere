package com.teamsphere.dto.department;

import jakarta.validation.constraints.NotEmpty;

public record DepartmentSearchRequest(@NotEmpty String query) {
}
