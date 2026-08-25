package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void save_shouldDelegateToService() {
        ProjectSaveDto dto = new ProjectSaveDto("","");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        doReturn(expectedResponse).when(projectService).saveProject(dto);

        ResponseEntity<?> result = projectController.save(dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(projectService).saveProject(dto);
    }

    @Test
    void search_shouldDelegateToService() {
        Page<Project> expectedPage = Page.empty();
        when(projectService.searchProject("test", 0)).thenReturn(expectedPage);

        Page<Project> result = projectController.search("test", 0);

        assertThat(result).isEqualTo(expectedPage);
        verify(projectService).searchProject("test", 0);
    }

    @Test
    void get_shouldDelegateToService() {
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        doReturn(expectedResponse).when(projectService).get(1L);

        ResponseEntity<?> result = projectController.get(1L);

        assertThat(result).isEqualTo(expectedResponse);
        verify(projectService).get(1L);
    }

    @Test
    void isOwner_shouldDelegateToService() {
        when(projectService.isOwner(1L)).thenReturn(true);

        boolean result = projectController.isOwner(1L);

        assertThat(result).isTrue();
        verify(projectService).isOwner(1L);
    }

    @Test
    void setFinished_shouldDelegateToService() {
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        doReturn(expectedResponse).when(projectService).setFinished(1L);

        ResponseEntity<?> result = projectController.setFinished(1L);

        assertThat(result).isEqualTo(expectedResponse);
        verify(projectService).setFinished(1L);
    }
}