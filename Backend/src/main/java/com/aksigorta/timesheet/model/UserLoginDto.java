package com.aksigorta.timesheet.model;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserLoginDto implements Serializable {
    @NotNull
    @Size(max = 128)
    @NotEmpty
    @NotBlank
    String username;
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*_\\-]).*$")
    @NotEmpty
    @NotBlank
    String password;
}