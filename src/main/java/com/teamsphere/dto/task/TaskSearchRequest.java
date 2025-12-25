package com.teamsphere.dto.task;

import jakarta.validation.constraints.NotEmpty;

public record TaskSearchRequest(@NotEmpty String query) {
}
