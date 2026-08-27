package com.aksigorta.timesheet.controller;


import com.aksigorta.timesheet.model.project.ProjectDurationDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void listTimeSheets_shouldDelegateToService() {
        Page<TimeSheetResponseDto> expectedPage = Page.empty();
        when(adminService.listTimeSheets(0)).thenReturn(expectedPage);

        Page<TimeSheetResponseDto> result = adminController.listTimeSheets(0);

        assertThat(result).isEqualTo(expectedPage);
        verify(adminService).listTimeSheets(0);
    }

    @Test
    void searchUser_shouldDelegateToService() {
        Page<UserResponseDto> expectedPage = Page.empty();
        when(adminService.searchUser(0, "test")).thenReturn(expectedPage);

        Page<UserResponseDto> result = adminController.searchUser(0, "test");

        assertThat(result).isEqualTo(expectedPage);
        verify(adminService).searchUser(0, "test");
    }

    @Test
    void searchTimeSheets_shouldDelegateToService() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        String username = "ali";

        Page<TimeSheetResponseDto> expectedPage = Page.empty();
        when(adminService.searchTimeSheet(0, username, startDate, endDate)).thenReturn(expectedPage);

        ResponseEntity<?> result = adminController.searchTimeSheets(0, username, startDate, endDate);

        assertThat(result.getBody()).isEqualTo(expectedPage);
        verify(adminService).searchTimeSheet(0, username, startDate, endDate);
    }

    @Test
    void getTopThree_shouldDelegateToService() {
        List<ProjectDurationDto> expectedList = List.of(new ProjectDurationDto(1L, "Project A", 480L));
        when(adminService.getTopThreeProjects()).thenReturn(expectedList);

        List<ProjectDurationDto> result = adminController.getTopThree();

        assertThat(result).isEqualTo(expectedList);
        verify(adminService).getTopThreeProjects();
    }
}