package com.aksigorta.timesheet.model.timeSheet;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link TimeSheet}
 */
@Data
public class TimeSheetResponseDto {

    Long id;

    LocalTime startTime;
    LocalTime endTime;
    String description;

    @PastOrPresent
    LocalDate date;

    UserResponseDto user;

    Project project;
}