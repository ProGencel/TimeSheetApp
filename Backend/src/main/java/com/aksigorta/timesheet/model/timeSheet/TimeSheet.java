package com.aksigorta.timesheet.model.timeSheet;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity(name = "timesheets")
public class TimeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long user_id;

    @PastOrPresent
    private LocalDate date;

    @PastOrPresent
    private LocalTime start_time;

    @PastOrPresent
    private LocalTime end_time;

    @Column(length = 1028)
    private String description;

}
