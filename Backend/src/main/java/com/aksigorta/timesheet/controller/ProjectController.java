package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectResponseDto;
import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("save")
    public ResponseEntity<?> save(@Valid @RequestBody ProjectSaveDto projectSaveDto)
    {
        return projectService.saveProject(projectSaveDto);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> get(@PathVariable Long id)
    {
        return projectService.get(id);
    }

    @GetMapping("isOwner/{id}")
    public boolean isOwner(@PathVariable Long id)
    {
        return projectService.isOwner(id);
    }

    @PutMapping("set_finished/{id}")
    public ResponseEntity<?> setFinished(@PathVariable Long id)
    {
        return projectService.setFinished(id);
    }

    @GetMapping("search")
    public ResponseEntity<?> searchProject(@RequestParam String q, @RequestParam int page) {
        return ResponseEntity.ok(projectService.searchMyProjects(q, page));
    }
}
