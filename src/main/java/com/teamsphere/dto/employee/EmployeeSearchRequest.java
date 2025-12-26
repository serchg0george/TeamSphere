package com.teamsphere.dto.employee;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for employees.
 *
 * @param query the search query string
 */
public record EmployeeSearchRequest(@NotEmpty String query) {
}
