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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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

    @GetMapping({"search_timesheets/{userId}","search_timesheets"})
    public ResponseEntity<?> searchTimeSheets(@PathVariable(required = false) Long userId,
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
            List<TimeSheetResponseDto> results = adminService.searchTimeSheetForExport(userId,localDate);
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
