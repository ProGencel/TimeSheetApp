package com.aksigorta.timesheet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "userEntity")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 128,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany
    private List<TimeSheet> timeSheetList;
}
