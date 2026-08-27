package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.ProjectRepository;
import com.aksigorta.timesheet.repository.TimeSheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSheetServiceTest {

    @Spy
    @InjectMocks
    private TimeSheetService timeSheetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimeSheetRepository timeSheetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    void save_shouldReturnBadRequest_whenUserNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        TimeSheetSaveDto dto = new TimeSheetSaveDto();

        // ACT
        ResponseEntity<?> result = timeSheetService.save(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void save_shouldReturnOk_whenProjectNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setStartTime(LocalTime.of(9,0));
        dto.setEndTime(LocalTime.of(17,0));
        dto.setProjectId(null);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT
        ResponseEntity<?> result = timeSheetService.save(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(timeSheetRepository).save(any(TimeSheet.class));
    }

    @Test
    void save_shouldReturnBadRequest_whenProjectNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setProjectId(99L);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<?> result = timeSheetService.save(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void getTimeSheetById_shouldReturnDto_whenTimeSheetBelongsToUser() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setId(5L);
        timeSheet.setUser(user);
        timeSheet.setDate(LocalDate.now());
        timeSheet.setStartTime(LocalTime.of(9, 0));
        timeSheet.setEndTime(LocalTime.of(17, 0));

        when(timeSheetRepository.findById(5L)).thenReturn(Optional.of(timeSheet));

        // ACT
        TimeSheetResponseDto result = timeSheetService.getTimeSheetById(5L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void getTimeSheetById_shouldReturnNull_whenTimeSheetNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        TimeSheetResponseDto result = timeSheetService.getTimeSheetById(99L);

        // ASSERT

        assertThat(result).isNull();
    }

    @Test
    void getTimeSheetById_shouldReturnNull_whenTimeSheetBelongsToAnotherUser() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User owner = new User();
        owner.setId(2L);

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setId(7L);
        timeSheet.setUser(owner);

        when(timeSheetRepository.findById(7L)).thenReturn(Optional.of(timeSheet));

        // ACT
        TimeSheetResponseDto result = timeSheetService.getTimeSheetById(7L);

        // ASSERT
        assertThat(result).isNull();
    }

    @Test
    void getWeeklyWorkHours_shouldReturnTotalMinutes_whenTimeSheetsExist() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        TimeSheet t1 = new TimeSheet();
        t1.setStartTime(LocalTime.of(9, 0));
        t1.setEndTime(LocalTime.of(17, 0));

        TimeSheet t2 = new TimeSheet();
        t2.setStartTime(LocalTime.of(10, 0));
        t2.setEndTime(LocalTime.of(12, 30));

        when(timeSheetRepository.findByUser_IdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(t1, t2));

        // ACT
        Long result = timeSheetService.getWeeklyWorkHours();

        // ASSERT
        assertThat(result).isEqualTo(630L);
    }

    @Test
    void getWeeklyWorkHours_shouldReturnZero_whenNoTimeSheetsExist() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        when(timeSheetRepository.findByUser_IdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // ACT
        Long result = timeSheetService.getWeeklyWorkHours();

        // ASSERT
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void updateTimeSheet_shouldReturnOk_whenValidUpdate() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        Project oldProject = new Project();
        oldProject.setId(10L);
        oldProject.setFinished(false);

        TimeSheet existingTimeSheet = new TimeSheet();
        existingTimeSheet.setId(5L);
        existingTimeSheet.setUser(user);
        existingTimeSheet.setProject(oldProject);

        when(timeSheetRepository.findById(5L)).thenReturn(Optional.of(existingTimeSheet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Project newProject = new Project();
        newProject.setId(20L);
        when(projectRepository.findById(20L)).thenReturn(Optional.of(newProject));

        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setProjectId(20L);

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(5L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(timeSheetRepository).save(existingTimeSheet);
        assertThat(existingTimeSheet.getProject()).isEqualTo(newProject);
    }

    @Test
    void updateTimeSheet_shouldReturnBadRequest_whenUserNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        TimeSheetSaveDto dto = new TimeSheetSaveDto();

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(5L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void updateTimeSheet_shouldReturnBadRequest_whenTimeSheetNotFound() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        TimeSheetSaveDto dto = new TimeSheetSaveDto();

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(99L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void updateTimeSheet_shouldReturnBadRequest_whenProjectAlreadyFinished() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        Project finishedProject = new Project();
        finishedProject.setId(10L);
        finishedProject.setFinished(true);

        TimeSheet existingTimeSheet = new TimeSheet();
        existingTimeSheet.setId(5L);
        existingTimeSheet.setUser(user);
        existingTimeSheet.setProject(finishedProject);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timeSheetRepository.findById(5L)).thenReturn(Optional.of(existingTimeSheet));

        TimeSheetSaveDto dto = new TimeSheetSaveDto();

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(5L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void updateTimeSheet_shouldReturnBadRequest_whenEndTimeBeforeStartTime() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(10L);
        project.setFinished(false);

        TimeSheet existingTimeSheet = new TimeSheet();
        existingTimeSheet.setId(5L);
        existingTimeSheet.setUser(user);
        existingTimeSheet.setProject(project);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timeSheetRepository.findById(5L)).thenReturn(Optional.of(existingTimeSheet));

        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setStartTime(LocalTime.of(17, 0));
        dto.setEndTime(LocalTime.of(9, 0));

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(5L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void updateTimeSheet_shouldReturnBadRequest_whenTimeSheetBelongsToAnotherUser() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Project project = new Project();
        project.setId(10L);
        project.setFinished(false);

        TimeSheet existingTimeSheet = new TimeSheet();
        existingTimeSheet.setId(5L);
        existingTimeSheet.setUser(owner); // başkasına ait
        existingTimeSheet.setProject(project);

        when(userRepository.findById(1L)).thenReturn(Optional.of(loggedInUser));
        when(timeSheetRepository.findById(5L)).thenReturn(Optional.of(existingTimeSheet));

        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));

        // ACT
        ResponseEntity<?> result = timeSheetService.updateTimeSheet(5L, dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(timeSheetRepository, never()).save(any(TimeSheet.class));
    }

    @Test
    void listTimeSheets_shouldReturnMappedPage_whenTimeSheetsExist() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        TimeSheet t1 = new TimeSheet();
        t1.setId(1L);
        t1.setUser(user);
        t1.setDate(LocalDate.now());
        t1.setStartTime(LocalTime.of(9, 0));
        t1.setEndTime(LocalTime.of(17, 0));

        TimeSheet t2 = new TimeSheet();
        t2.setId(2L);
        t2.setUser(user);
        t2.setDate(LocalDate.now().minusDays(1));
        t2.setStartTime(LocalTime.of(10, 0));
        t2.setEndTime(LocalTime.of(18, 0));

        Page<TimeSheet> timeSheetPage = new PageImpl<>(List.of(t1, t2));
        when(timeSheetRepository.findByUserIdEquals(eq(1L), any(Pageable.class)))
                .thenReturn(timeSheetPage);

        // ACT
        Page<TimeSheetResponseDto> result = timeSheetService.listTimeSheets(0);

        // ASSERT
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
    }

    @Test
    void listTimeSheets_shouldReturnEmptyPage_whenNoTimeSheetsExist() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        Page<TimeSheet> emptyPage = new PageImpl<>(List.of());
        when(timeSheetRepository.findByUserIdEquals(eq(1L), any(Pageable.class)))
                .thenReturn(emptyPage);

        // ACT
        Page<TimeSheetResponseDto> result = timeSheetService.listTimeSheets(0);

        // ASSERT
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void searchTimeSheets_shouldReturnMappedPage_whenTimeSheetsExistInRange() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        User user = new User();
        user.setId(1L);

        TimeSheet t1 = new TimeSheet();
        t1.setId(1L);
        t1.setUser(user);
        t1.setDate(LocalDate.now());
        t1.setStartTime(LocalTime.of(9, 0));
        t1.setEndTime(LocalTime.of(17, 0));

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        Page<TimeSheet> timeSheetPage = new PageImpl<>(List.of(t1));
        when(timeSheetRepository.findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(
                eq(1L), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(timeSheetPage);

        // ACT
        Page<TimeSheetResponseDto> result = timeSheetService.searchTimeSheets(0, startDate, endDate);

        // ASSERT
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void searchTimeSheets_shouldReturnEmptyPage_whenNoTimeSheetsInRange() {
        // ARRANGE
        doReturn(1L).when(timeSheetService).getCurrentUserId();

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        Page<TimeSheet> emptyPage = new PageImpl<>(List.of());
        when(timeSheetRepository.findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(
                eq(1L), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(emptyPage);

        // ACT
        Page<TimeSheetResponseDto> result = timeSheetService.searchTimeSheets(0, startDate, endDate);

        // ASSERT
        assertThat(result.getContent()).isEmpty();
    }
}