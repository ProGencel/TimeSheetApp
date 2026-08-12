package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("save")
    public ResponseEntity<?> save(@Valid @RequestBody ProjectSaveDto projectSaveDto)
    {
        return projectService.saveProject(projectSaveDto);
    }

}
