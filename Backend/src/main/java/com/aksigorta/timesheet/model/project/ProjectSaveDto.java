package com.aksigorta.timesheet.model.project;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Project}
 */
@Value
public class ProjectSaveDto implements Serializable {
    String name;
    String description;
}