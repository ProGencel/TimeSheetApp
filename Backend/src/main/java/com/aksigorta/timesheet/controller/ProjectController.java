package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.ProjectResponseDto;
import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("search")
    public Page<ProjectResponseDto> search(@RequestParam(defaultValue = "") String q,
                                           @RequestParam(defaultValue = "0") int page)
    {
        return projectService.searchProject(q,page);
    }
}
