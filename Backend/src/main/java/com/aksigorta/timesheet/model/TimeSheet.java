package com.aksigorta.timesheet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class TimeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private User owner;

    @PastOrPresent
    private LocalDate localDate;

    @PastOrPresent
    private LocalTime beginTime;

    @PastOrPresent
    private LocalTime endTime;

    @Column(length = 1028)
    private String description;

}
