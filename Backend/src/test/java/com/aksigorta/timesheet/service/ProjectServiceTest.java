package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.ProjectRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    @Spy
    private ProjectService projectService;

    @Test
    void saveProject_shouldReturnOk_whenNameIsAvailable() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        when(projectRepository.findByName("Backend Refactor")).thenReturn(Optional.empty());

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProjectSaveDto dto = new ProjectSaveDto();
        dto.setName("Backend Refactor");

        // ACT
        ResponseEntity<?> result = projectService.saveProject(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void saveProject_shouldReturnBadRequest_whenNameAlreadyTaken() {
        // ARRANGE

        Project existingProject = new Project();
        existingProject.setId(5L);
        existingProject.setName("Backend Refactor");

        when(projectRepository.findByName("Backend Refactor")).thenReturn(Optional.of(existingProject));

        ProjectSaveDto dto = new ProjectSaveDto();
        dto.setName("Backend Refactor");

        // ACT
        ResponseEntity<?> result = projectService.saveProject(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(projectRepository, never()).save(any(Project.class));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void searchProject_shouldReturnMatchingPage_whenProjectsFound() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        Project p1 = new Project();
        p1.setId(1L);
        p1.setName("Backend Refactor");

        Project p2 = new Project();
        p2.setId(2L);
        p2.setName("Backend Migration");

        Page<Project> projectPage = new PageImpl<>(List.of(p1, p2));

        when(projectRepository.findByNameContainsIgnoreCaseAndMember(eq("backend"), eq(1L), any(Pageable.class)))
                .thenReturn(projectPage);

        // ACT
        Page<Project> result = projectService.searchMyProjects("backend", 0);

        // ASSERT
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(p1, p2);
    }

    @Test
    void searchProject_shouldReturnEmptyPage_whenNoMatchFound() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        Page<Project> emptyPage = new PageImpl<>(List.of());

        when(projectRepository.findByNameContainsIgnoreCaseAndMember(eq("nonexistent"), eq(1L), any(Pageable.class)))
                .thenReturn(emptyPage);

        // ACT
        Page<Project> result = projectService.searchMyProjects("nonexistent", 0);

        // ASSERT
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void get_shouldReturnOkWithProject_whenProjectExists() {
        // ARRANGE
        Project project = new Project();
        project.setId(1L);
        project.setName("Backend Refactor");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // ACT
        ResponseEntity<?> result = projectService.get(1L);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(project);
    }

    @Test
    void get_shouldReturnBadRequest_whenProjectNotFound() {
        // ARRANGE
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<?> result = projectService.get(99L);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void isOwner_shouldReturnTrue_whenCurrentUserIsOwner() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(10L);
        project.setUser(owner);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        // ACT
        boolean result = projectService.isOwner(10L);

        // ASSERT
        assertThat(result).isTrue();
    }

    @Test
    void isOwner_shouldReturnFalse_whenCurrentUserIsNotOwner() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        User owner = new User();
        owner.setId(2L);

        Project project = new Project();
        project.setId(10L);
        project.setUser(owner);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        // ACT
        boolean result = projectService.isOwner(10L);

        // ASSERT
        assertThat(result).isFalse();
    }

    @Test
    void setFinished_shouldReturnOk_whenUserIsOwner() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(10L);
        project.setUser(owner);
        project.setFinished(false);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        // ACT
        ResponseEntity<?> result = projectService.setFinished(10L);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(project.isFinished()).isTrue();
        verify(projectRepository).save(project);
    }

    @Test
    void setFinished_shouldReturnBadRequest_whenUserIsNotOwner() {
        // ARRANGE
        doReturn(1L).when(projectService).getCurrentUserId();

        User owner = new User();
        owner.setId(2L);

        Project project = new Project();
        project.setId(10L);
        project.setUser(owner);
        project.setFinished(false);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        // ACT
        ResponseEntity<?> result = projectService.setFinished(10L);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(project.isFinished()).isFalse();
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void setFinished_shouldReturnBadRequest_whenProjectNotFound() {
        // ARRANGE
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<?> result = projectService.setFinished(99L);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(projectRepository, never()).save(any(Project.class));
    }

}