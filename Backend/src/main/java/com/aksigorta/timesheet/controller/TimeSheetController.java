package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.service.TimeSheetService;
import jakarta.validation.Valid;
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
@RequestMapping("timesheet")
@RequiredArgsConstructor
public class TimeSheetController {

    private final TimeSheetService timeSheetService;

    @PostMapping("save")
    public ResponseEntity<?> save(@Valid @RequestBody TimeSheetSaveDto timeSheetSaveDto)
    {
        return timeSheetService.save(timeSheetSaveDto);
    }

    @GetMapping("list")
    public Page<TimeSheetResponseDto> list(@RequestParam(defaultValue = "0") int page)
    {
        return timeSheetService.listTimeSheets(page);
    }

    @GetMapping("get/{id}")
    public TimeSheetResponseDto get(@PathVariable Long id)
    {
        return timeSheetService.getTimeSheetById(id);
    }

    @GetMapping("search")
    public Page<TimeSheetResponseDto> search(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam LocalDate startDate,
                                             @RequestParam LocalDate endDate)
    {
        return timeSheetService.searchTimeSheets(page,startDate,endDate);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody  TimeSheetSaveDto timeSheetSaveDto)
    {
        return timeSheetService.updateTimeSheet(id,timeSheetSaveDto);
    }

    @GetMapping("export")
    public ResponseEntity<?> export(@RequestParam(required = false) LocalDate startDate,
                                    @RequestParam(required = false) LocalDate endDate,
                                    @RequestParam(defaultValue = "csv") String format) throws IOException {

        List<TimeSheetResponseDto> results = timeSheetService.searchTimeSheetsForExport(startDate,endDate);

        byte[] fileContent;
        String fileName;
        MediaType mediaType;

        if("excel".equalsIgnoreCase(format))
        {
            fileContent = timeSheetService.toExcel(results);
            fileName = "timesheets.xlsx";
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        else
        {
            fileContent = timeSheetService.toCsv(results);
            fileName = "timesheets.csv";
            mediaType = MediaType.parseMediaType("text/csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(mediaType)
                .body(fileContent);

    }

}
