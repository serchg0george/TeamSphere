package com.teamsphere.dto.position;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for positions.
 *
 * @param query the search query string
 */
public record PositionSearchRequest(@NotEmpty String query) {
}
