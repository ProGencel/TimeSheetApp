package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectDurationDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("list")
    public Page<TimeSheetResponseDto> listTimeSheets(@RequestParam(defaultValue = "0") int page)
    {
        return adminService.listTimeSheets(page);
    }

    @GetMapping("search_user")
    public Page<UserResponseDto> searchUser(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "") String q)
    {
        return adminService.searchUser(page,q);
    }

    @GetMapping("search_timesheets")
    public ResponseEntity<?> searchTimeSheets(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(required = false) String username,
                                              @RequestParam(required = false) LocalDate startDate,
                                              @RequestParam(required = false) LocalDate endDate)
    {
        Page<TimeSheetResponseDto> result = adminService.searchTimeSheet(page, username, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("get_top_three")
    public List<ProjectDurationDto> getTopThree()
    {
        return adminService.getTopThreeProjects();
    }

    @GetMapping("weekly-hours/{userId}")
    public ResponseEntity<?> getWeeklyHours(@PathVariable Long userId)
    {
        return adminService.getWeeklyWorkHoursByUserId(userId);
    }

    @GetMapping("export")
    public ResponseEntity<?> export(@RequestParam(defaultValue = "csv") String exportFormat,
                                    @RequestParam(defaultValue = "") String q,
                                    @RequestParam(required = false) LocalDate startDate,
                                    @RequestParam(required = false) LocalDate endDate,
                                    @RequestParam(defaultValue = "user") String inputFormat) throws IOException
    {
        byte[] fileContent = null;
        MediaType mediaType = null;
        String fileName = null;

        if("user".equalsIgnoreCase(inputFormat))
        {
            List<UserResponseDto> results = adminService.searchUserForExport(q);
            if("csv".equalsIgnoreCase(exportFormat))
            {
                fileContent = adminService.toCsv(results);
                fileName = "users.csv";
                mediaType = MediaType.parseMediaType("text/csv");
            }
            else
            {
                fileContent = adminService.toExcel(results);
                fileName = "users.xlsx";
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
        }
        else if("timesheet".equalsIgnoreCase(inputFormat))
        {
            List<TimeSheetResponseDto> results = adminService.searchTimeSheetForExport(q, startDate, endDate);
            if("csv".equalsIgnoreCase(exportFormat))
            {
                fileContent = adminService.toCsv(results);
                fileName = "timesheets.csv";
                mediaType = MediaType.parseMediaType("text/csv");
            }
            else
            {
                fileContent = adminService.toExcel(results);
                fileName = "timesheet.xlsx";
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + fileName)
                .contentType(mediaType)
                .body(fileContent);
    }

}
