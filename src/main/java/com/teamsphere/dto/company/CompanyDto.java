package com.teamsphere.dto.company;

import com.teamsphere.dto.BaseDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyDto extends BaseDto {

    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 50)
    private String industry;

    @NotBlank
    @Size(min = 1, max = 50)
    private String address;

    @NotBlank
    @NotNull
    @Email
    @Size(min = 1, max = 50)
    private String email;


}
