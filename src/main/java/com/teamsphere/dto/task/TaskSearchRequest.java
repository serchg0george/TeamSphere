package com.teamsphere.dto.task;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for tasks.
 *
 * @param query the search query string
 */
public record TaskSearchRequest(@NotEmpty String query) {
}
