package com.aksigorta.timesheet.model.user;

import com.aksigorta.timesheet.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for {@link User}
 */
@Data
public class UserResponseDto{
    @NotNull
    Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    String username;
    @NotNull
    @NotEmpty
    @NotBlank
    String email;
    @NotNull
    Role role;
}