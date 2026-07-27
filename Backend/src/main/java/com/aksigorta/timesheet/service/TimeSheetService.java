package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.TimeSheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public Long getCurrentUserId()
    {
        return 1L;
    }

    public ResponseEntity<?> save(TimeSheetSaveDto timeSheetSaveDto)
    {
        // TODO: add JWT authentication
        Long userId = getCurrentUserId();

        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent())
        {
            if(timeSheetSaveDto.getDate().equals(LocalDate.now()))
            {
                LocalTime startTime = timeSheetSaveDto.getStartTime();
                LocalTime endTime = timeSheetSaveDto.getEndTime();

                boolean isPast = startTime.isAfter(LocalTime.now()) || endTime.isAfter(LocalTime.now());

                if(isPast)
                {
                    Map<String,Object> errorMap = Map.of("Success",false,"Error Message:","Please enter a valid time");
                    return ResponseEntity.badRequest().body(errorMap);
                }
            }
            TimeSheet timeSheet = modelMapper.map(timeSheetSaveDto,TimeSheet.class);
            timeSheet.setUserId(userId);
            timeSheetRepository.save(timeSheet);
            return ResponseEntity.ok().body(timeSheet);
        }
        Map<String,Object> errorMap = Map.of("Error Message: ","Please login first");
        return ResponseEntity.badRequest().body(errorMap);
    }

    public Page<TimeSheetResponseDto> listTimeSheets(int page)
    {
        // TODO: add JWT authentication
        Long userid = getCurrentUserId();

        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<TimeSheet> timeSheetPage = timeSheetRepository.findByUserIdEquals(userid,pageable);

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return timeSheetResponseDtoPage;
    }

    public Page<TimeSheetResponseDto> searchTimeSheets(int page, LocalDate startDate, LocalDate endDate)
    {
        // TODO: add JWT authentication
        Long userId = getCurrentUserId();

        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<TimeSheet> timeSheetPage = timeSheetRepository.
                findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(userId,startDate,endDate,pageable);

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return timeSheetResponseDtoPage;
    }

    public List<TimeSheetResponseDto> searchTimeSheetsForExport(LocalDate startDate,LocalDate endDate)
    {
        // TODO: add JWT authentication
        Long userId = getCurrentUserId();

        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        List<TimeSheet> timeSheetList = timeSheetRepository.findAllForExport(userId,startDate,endDate,sort);

        List<TimeSheetResponseDto> timeSheetResponseDtoList = timeSheetList.stream().map((element) -> modelMapper.map(element, TimeSheetResponseDto.class)).toList();
        return timeSheetResponseDtoList;

    }

    public ResponseEntity<?> updateTimeSheet(Long id,TimeSheetSaveDto timeSheetSaveDto)
    {
        Optional<TimeSheet> timeSheetOptional = timeSheetRepository.findById(id);

        if(timeSheetOptional.isPresent())
        {
            Long user_id = getCurrentUserId();

            TimeSheet timeSheet = timeSheetOptional.get();
            if(timeSheet.getUserId().equals(user_id))
            {
                modelMapper.map(timeSheetSaveDto,timeSheet);
                timeSheet.setUserId(user_id);
                timeSheet.setId(id);
                timeSheetRepository.save(timeSheet);
                return ResponseEntity.ok().body(timeSheet);
            }
            return ResponseEntity.badRequest().body(Map.of("Success: ",false,"Error Message: ","This timesheet does not belongs to you"));
        }
        return ResponseEntity.badRequest().body(Map.of("Success: ",false,"Error Message: ","Please try again with present timesheet"));
    }

    public byte[] toCsv(List<TimeSheetResponseDto> timeSheetList)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tarih,Başlangıç,Bitiş,Açıklama\n");

        for(TimeSheetResponseDto t : timeSheetList)
        {
            sb.append(t.getDate()).append(",")
                    .append(t.getStartTime()).append(",")
                    .append(t.getEndTime()).append(",")
                    .append(escapeCsv(t.getDescription())).append(",")
                    .append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toExcel(List<TimeSheetResponseDto> timeSheetList) throws IOException
    {
        try(Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Timesheets");

            Row header = sheet.createRow(0);
            String[] columns = {"Tarih","Başlangıç","Bitiş","Açıklama"};
            for(int i = 0;i<columns.length;i++)
            {
                header.createCell(i).setCellValue(columns[i]);
            }
            int rowNum = 1;
            for(TimeSheetResponseDto t : timeSheetList)
            {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getDate().toString());
                row.createCell(1).setCellValue(t.getStartTime().toString());
                row.createCell(2).setCellValue(t.getEndTime().toString());
                row.createCell(3).setCellValue(t.getDescription());
            }

            for(int i = 0;i<columns.length;i++)
            {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String escapeCsv(String value)
    {
        if(value == null)
        {
            return "";
        }
        if(value.contains(",") || value.contains("\"") || value.contains("\n"))
        {
            return "\"" + value.replace("\"","\"\"") + "\"";
        }
        return value;
    }

}
