package com.aksigorta.timesheet.model.project;

import com.aksigorta.timesheet.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    private User user;

    private Long[] member_ids;

    private boolean isFinished = false;

}