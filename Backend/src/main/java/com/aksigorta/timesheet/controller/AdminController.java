package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("search_user")
    public Page<UserResponseDto> searchUser(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "") String q)
    {
        return adminService.searchUser(page,q);
    }

    @GetMapping("search_timesheets/{userId}")
    public ResponseEntity<?> searchTimeSheets(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(required = false) LocalDate localDate)
    {
        return adminService.searchTimeSheet(page,userId,localDate);
    }

    @GetMapping({"export","export/{userId}"})
    public ResponseEntity<?> export(@PathVariable(required = false) Long userId,
                                    @RequestParam(defaultValue = "csv") String exportFormat,
                                    @RequestParam(defaultValue = "") String q,
                                    @RequestParam(required = false) LocalDate localDate,
                                    @RequestParam(defaultValue = "user") String inputFormat)
    {
        byte[] fileContent = null;
        MediaType mediaType = null;
        String fileName = null;

        if("user".equalsIgnoreCase(inputFormat))
        {
            if("csv".equalsIgnoreCase(exportFormat))
            {
                List<UserResponseDto> results = adminService.searchUserForExport(q);
                fileContent = adminService.toCsv(results);
                fileName = "users.csv";
                mediaType = MediaType.parseMediaType("text/csv");
            }
        }
        else if("timesheet".equalsIgnoreCase(inputFormat))
        {
            if("csv".equalsIgnoreCase(exportFormat))
            {
                List<TimeSheetResponseDto> results = adminService.searchTimeSheetForExport(userId,localDate);
                fileContent = adminService.toCsv(results);
                fileName = "timesheets.csv";
                mediaType = MediaType.parseMediaType("text/csv");
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + fileName)
                .contentType(mediaType)
                .body(fileContent);
    }

}
