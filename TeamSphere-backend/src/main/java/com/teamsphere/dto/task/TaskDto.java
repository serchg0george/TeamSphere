package com.teamsphere.dto.task;

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
public class TaskDto extends BaseDto {

    @NotBlank
    @NotNull
    @Size(max = 20)
    private String taskStatus;

    @NotNull
    @Min(value = 0, message = "Time can't be negative")
    @Max(value = 480, message = "Value can't be greater than 10 digits")
    private Integer timeSpentMinutes;

    @NotBlank
    @NotNull
    @Size(min = 1, max = 150)
    private String taskDescription;

    @NotBlank
    @NotNull
    @Size(max = 20)
    private String taskNumber;

}
