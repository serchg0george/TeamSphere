package com.teamsphere.dto.employee;

import com.teamsphere.dto.BaseDto;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class EmployeeDto extends BaseDto {

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\p{IsCyrillic} -]+$")
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\p{IsCyrillic} -]+$")
    private String lastName;

    @Pattern(regexp = "^(?!\\s*$)[-0-9\\s]{10}$")
    @Size(max = 10)
    private String pin;

    @NotBlank
    @Size(max = 50)
    private String address;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id must be a positive number")
    @Min(value = 1, message = "Id cannot be lower than 1")
    private Long departmentId;

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id must be a positive number")
    @Min(value = 1, message = "Id cannot be lower than 1")
    private Long positionId;

    private String departmentName;

    private String positionName;

    private List<TaskInfo> tasks;

    private List<ProjectInfo> projects;

}