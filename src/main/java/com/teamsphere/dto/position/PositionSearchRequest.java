package com.teamsphere.dto.position;

import jakarta.validation.constraints.NotEmpty;

public record PositionSearchRequest(@NotEmpty String query) {
}
