package com.teamsphere.dto.project;

import jakarta.validation.constraints.NotEmpty;

public record ProjectSearchRequest(@NotEmpty String query) {
}
