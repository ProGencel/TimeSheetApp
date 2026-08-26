package com.aksigorta.timesheet.model.project;

import com.aksigorta.timesheet.model.user.UserResponseDto;
import lombok.Data;

/**
 * DTO for {@link Project}
 */
@Data
public class ProjectSaveDto{
    String name;
    String description;

    Long[] memberIds;

}