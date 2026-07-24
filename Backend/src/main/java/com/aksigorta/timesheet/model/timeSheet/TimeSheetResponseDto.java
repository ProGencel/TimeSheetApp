package com.aksigorta.timesheet.model.timeSheet;

import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link TimeSheet}
 */
@Data
public class TimeSheetResponseDto {
    @PastOrPresent
    LocalDate date;
    @PastOrPresent
    LocalTime start_time;
    @PastOrPresent
    LocalTime end_time;
    String description;
}