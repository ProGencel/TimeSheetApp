package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    Optional<Project> findByName(String name);

    Page<Project> findByNameContains(String name, Pageable pageable);
}
