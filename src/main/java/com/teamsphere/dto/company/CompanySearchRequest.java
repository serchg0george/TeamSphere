package com.teamsphere.dto.company;

import jakarta.validation.constraints.NotEmpty;

public record CompanySearchRequest(@NotEmpty String query) {
}
