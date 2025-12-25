package com.teamsphere.dto.project;

import com.teamsphere.dto.BaseDto;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProjectDto extends BaseDto {

    @NotBlank
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @NotNull
    @Size(min = 1, max = 500)
    private String description;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD")
    private String startDate;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD")
    private String finishDate;

    @NotBlank
    @NotNull
    private String status;

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id must be a positive number")
    @Min(value = 1, message = "Id cannot be lower than 1")
    private Long companyId;

    private String companyName;
}
