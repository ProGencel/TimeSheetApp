package com.aksigorta.timesheet.model.project;

import lombok.Data;

/**
 * DTO for {@link Project}
 */
@Data
public class ProjectSaveDto{
    String name;
    String description;

    Long[] member_ids;

}