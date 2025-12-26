package com.teamsphere.dto.project;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for projects.
 *
 * @param query the search query string
 */
public record ProjectSearchRequest(@NotEmpty String query) {
}
