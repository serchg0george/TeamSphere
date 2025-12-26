package com.teamsphere.dto.company;

import jakarta.validation.constraints.NotEmpty;

/**
 * Search request for companies.
 *
 * @param query the search query string
 */
public record CompanySearchRequest(@NotEmpty String query) {
}
