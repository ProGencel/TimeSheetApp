package com.aksigorta.timesheet.model;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserRegisterDto implements Serializable {
    @NotNull
    @Size(max = 128)
    @NotEmpty
    @NotBlank
    String username;
    @NotNull
    @Email
    @NotEmpty
    @NotBlank
    String email;
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*_\\-]).*$",message = "Please try again with different password")
    @NotEmpty
    @NotBlank
    String password;
}