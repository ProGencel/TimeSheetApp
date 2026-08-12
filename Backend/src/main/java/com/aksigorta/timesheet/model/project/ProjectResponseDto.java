package com.aksigorta.timesheet.model.project;

import com.aksigorta.timesheet.model.user.UserResponseDto;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Project}
 */
@Data
public class ProjectResponseDto {
    String name;
    String description;
    UserResponseDto user;
    boolean isFinished = false;

}