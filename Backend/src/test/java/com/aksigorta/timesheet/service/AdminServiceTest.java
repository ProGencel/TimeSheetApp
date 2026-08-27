package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectDurationDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.TimeSheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import com.aksigorta.timesheet.service.TimeSheetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private TimeSheetRepository timeSheetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getTopThreeProjects_shouldReturnTopThreeSortedByDuration() {
        Project projectA = new Project();
        projectA.setId(1L);
        projectA.setName("Project A");

        Project projectB = new Project();
        projectB.setId(2L);
        projectB.setName("Project B");

        TimeSheet t1 = new TimeSheet();
        t1.setProject(projectA);
        t1.setStartTime(LocalTime.of(9, 0));
        t1.setEndTime(LocalTime.of(17, 0));

        TimeSheet t2 = new TimeSheet();
        t2.setProject(projectB);
        t2.setStartTime(LocalTime.of(9, 0));
        t2.setEndTime(LocalTime.of(11, 0));

        when(timeSheetRepository.findAll()).thenReturn(List.of(t1, t2));

        List<ProjectDurationDto> result = adminService.getTopThreeProjects();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).projectName()).isEqualTo("Project A");
        assertThat(result.get(0).minutes()).isEqualTo(480L);
        assertThat(result.get(1).projectName()).isEqualTo("Project B");
    }

    @Test
    void getTopThreeProjects_shouldSkipTimeSheetsWithoutProject() {
        TimeSheet withoutProject = new TimeSheet();
        withoutProject.setProject(null);
        withoutProject.setStartTime(LocalTime.of(9, 0));
        withoutProject.setEndTime(LocalTime.of(10, 0));

        when(timeSheetRepository.findAll()).thenReturn(List.of(withoutProject));

        List<ProjectDurationDto> result = adminService.getTopThreeProjects();

        assertThat(result).isEmpty();
    }

    @Test
    void searchUser_shouldReturnMatchingUsers() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");

        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findByUsernameContainsIgnoreCaseOrEmailEquals(eq("test"), eq("test"), any(Pageable.class)))
                .thenReturn(userPage);

        Page<UserResponseDto> result = adminService.searchUser(0, "test");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void searchTimeSheet_shouldUseUsernameAndDateRangeFilter_whenAllProvided() {
        TimeSheet t = new TimeSheet();
        Page<TimeSheet> page = new PageImpl<>(List.of(t));
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        String username = "ali";

        when(timeSheetRepository.searchByUsernameAndDateRange(eq(username), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(page);

        Page<TimeSheetResponseDto> result = adminService.searchTimeSheet(0, username, startDate, endDate);

        assertThat(result).isNotNull();
        verify(timeSheetRepository).searchByUsernameAndDateRange(eq(username), eq(startDate), eq(endDate), any(Pageable.class));
    }

    @Test
    void searchTimeSheet_shouldPassNullFilters_whenNoFiltersProvided() {
        Page<TimeSheet> page = new PageImpl<>(List.of(new TimeSheet()));

        when(timeSheetRepository.searchByUsernameAndDateRange(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        Page<TimeSheetResponseDto> result = adminService.searchTimeSheet(0, null, null, null);

        assertThat(result).isNotNull();
        verify(timeSheetRepository).searchByUsernameAndDateRange(isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void listTimeSheets_shouldReturnAllTimeSheetsPaged() {
        Page<TimeSheet> page = new PageImpl<>(List.of(new TimeSheet()));
        when(timeSheetRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TimeSheetResponseDto> result = adminService.listTimeSheets(0);

        assertThat(result.getContent()).hasSize(1);
        verify(timeSheetRepository).findAll(any(Pageable.class));
    }
}