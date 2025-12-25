package com.teamsphere.dto.position;

import com.teamsphere.dto.BaseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto extends BaseDto {

    @NotBlank(message = "Should not be blank")
    @NotNull
    @Size(min = 1, max = 50)
    private String positionName;

    @NotNull(message = "Must not be null")
    @Min(value = 0, message = "Can't be negative number")
    private Integer yearsOfExperience;

}
