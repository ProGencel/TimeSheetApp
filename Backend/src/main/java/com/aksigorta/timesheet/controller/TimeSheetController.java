package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.service.TimeSheetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @GetMapping("search")
    public Page<TimeSheetResponseDto> search(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam LocalDate startDate,
                                             @RequestParam LocalDate endDate)
    {
        return timeSheetService.searchTimeSheets(page,startDate,endDate);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid TimeSheetSaveDto timeSheetSaveDto)
    {
        return timeSheetService.updateTimeSheet(id,timeSheetSaveDto);
    }

}
