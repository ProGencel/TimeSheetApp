package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.service.TimeSheetService;
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
class TimeSheetControllerTest {

    @Mock
    private TimeSheetService timeSheetService;

    @InjectMocks
    private TimeSheetController timeSheetController;

    @Test
    void save_shouldDelegateToService() {
        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        doReturn(expectedResponse).when(timeSheetService).save(dto);

        ResponseEntity<?> result = timeSheetController.save(dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(timeSheetService).save(dto);
    }

    @Test
    void list_shouldDelegateToService() {
        Page<TimeSheetResponseDto> expectedPage = Page.empty();
        when(timeSheetService.listTimeSheets(0)).thenReturn(expectedPage);

        Page<TimeSheetResponseDto> result = timeSheetController.list(0);

        assertThat(result).isEqualTo(expectedPage);
        verify(timeSheetService).listTimeSheets(0);
    }

    @Test
    void get_shouldDelegateToService() {
        TimeSheetResponseDto expectedDto = new TimeSheetResponseDto();
        when(timeSheetService.getTimeSheetById(1L)).thenReturn(expectedDto);

        TimeSheetResponseDto result = timeSheetController.get(1L);

        assertThat(result).isEqualTo(expectedDto);
        verify(timeSheetService).getTimeSheetById(1L);
    }

    @Test
    void update_shouldDelegateToService() {
        TimeSheetSaveDto dto = new TimeSheetSaveDto();
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();
        doReturn(expectedResponse).when(timeSheetService).updateTimeSheet(1L, dto);

        ResponseEntity<?> result = timeSheetController.update(1L, dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(timeSheetService).updateTimeSheet(1L, dto);
    }

    @Test
    void getDuration_shouldDelegateToService() {
        when(timeSheetService.getWeeklyWorkHours()).thenReturn(2400L);

        Long result = timeSheetController.getDuration();

        assertThat(result).isEqualTo(2400L);
        verify(timeSheetService).getWeeklyWorkHours();
    }
}