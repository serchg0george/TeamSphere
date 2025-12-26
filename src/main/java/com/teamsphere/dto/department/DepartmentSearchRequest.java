package com.teamsphere.dto.department;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for departments.
 *
 * @param query the search query string
 */
public record DepartmentSearchRequest(@NotEmpty String query) {
}
