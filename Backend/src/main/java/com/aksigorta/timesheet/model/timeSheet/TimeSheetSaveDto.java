package com.aksigorta.timesheet.model.timeSheet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link TimeSheet}
 */
@Data
public class TimeSheetSaveDto{
    @NotNull
    @PastOrPresent
    LocalDate date;
    @NotNull
    LocalTime startTime;
    @NotNull
    LocalTime endTime;
    @Size
    String description;
}