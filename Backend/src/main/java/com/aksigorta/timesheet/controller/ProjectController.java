package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.Project;
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
    public Page<Project> search(@RequestParam(defaultValue = "") String q,
                                @RequestParam(defaultValue = "0") int page)
    {
        return projectService.searchProject(q,page);
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
}
