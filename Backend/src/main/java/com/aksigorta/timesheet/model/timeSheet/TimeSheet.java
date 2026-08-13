package com.aksigorta.timesheet.model.timeSheet;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.persistence.Column;
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

    @PastOrPresent
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(length = 1028)
    private String description;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @ManyToOne
    private Project project;

}
