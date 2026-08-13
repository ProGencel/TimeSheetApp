package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.TimeSheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TimeSheetRepository timeSheetRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public Page<UserResponseDto> searchUser(int page, String q)
    {
        Sort sort = Sort.by(Sort.Direction.DESC,"username");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<User> userPage = userRepository.findByUsernameContainsIgnoreCaseOrEmailEquals(q,q,pageable);

        Page<UserResponseDto> userResponseDtoPage = userPage.map((element) -> modelMapper.map(element, UserResponseDto.class));

        return userResponseDtoPage;
    }

    public ResponseEntity<?> searchTimeSheet(int page, Long userId, LocalDate localDate)
    {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<TimeSheet> timeSheetPage;

        if (userId != null && localDate != null) {
            timeSheetPage = timeSheetRepository.findByUser_IdEqualsAndDateEquals(userId, localDate, pageable);
        } else if (userId != null) {
            timeSheetPage = timeSheetRepository.findByUser_IdEquals(userId, pageable);
        } else if (localDate != null) {
            timeSheetPage = timeSheetRepository.findByDateEquals(localDate, pageable);
        } else {
            timeSheetPage = timeSheetRepository.findAll(pageable);
        }

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return ResponseEntity.ok().body(timeSheetResponseDtoPage);
    }

    public Page<TimeSheetResponseDto> listTimeSheets(int page)
    {
        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<TimeSheet> timeSheetPage = timeSheetRepository.findAll(pageable);

        return getTimeSheetResponseDtos(timeSheetPage);
    }

    public byte[] toCsv(List<?> dataList)
    {
        if(dataList.isEmpty())
        {
            return new byte[0];
        }
        if(dataList.get(0) instanceof TimeSheetResponseDto)
        {
            StringBuilder sb = new StringBuilder();

            @SuppressWarnings("unchecked")
            List<TimeSheetResponseDto> timeSheetList = (List<TimeSheetResponseDto>) dataList;

            boolean singleUser = timeSheetList.stream()
                    .map(t -> t.getUser() != null ? t.getUser().getId() : null)
                    .distinct()
                    .count() == 1;

            if (singleUser && timeSheetList.get(0).getUser() != null) {
                String username = timeSheetList.get(0).getUser().getUsername();
                sb.append("User,").append(escapeCsv(username)).append("\n");
            }

            if (singleUser) {
                sb.append("Date,Start,End,Description,Project\n");
            } else {
                sb.append("User,Date,Start,End,Description,Project\n");
            }

            for(TimeSheetResponseDto t : timeSheetList)
            {
                if (!singleUser) {
                    String u = t.getUser() != null ? t.getUser().getUsername() : "";
                    sb.append(escapeCsv(u)).append(",");
                }
                sb.append(t.getDate()).append(",")
                        .append(t.getStartTime()).append(",")
                        .append(t.getEndTime()).append(",")
                        .append(escapeCsv(t.getDescription())).append(",")
                        .append(escapeCsv(t.getProject().getName()))
                        .append("\n");
            }
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
        if(dataList.get(0) instanceof UserResponseDto)
        {
            StringBuilder sb = new StringBuilder();

            @SuppressWarnings("unchecked")
            List<UserResponseDto> userResponseDtoList = (List<UserResponseDto>) dataList;

            sb.append("ID,Kullanıcı,Email,Rol\n");

            for(UserResponseDto u : userResponseDtoList)
            {
                sb.append(u.getId()).append(",")
                        .append(escapeCsv(u.getUsername())).append(",")
                        .append(escapeCsv(u.getEmail())).append(",")
                        .append(u.getRole())
                        .append("\n");
            }
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
        return new byte[0];
    }

    public byte[] toExcel(List<?> dataList) throws IOException
    {
        if(dataList.isEmpty())
        {
            return new byte[0];
        }
        try(Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if(dataList.get(0) instanceof TimeSheetResponseDto)
            {
                @SuppressWarnings("unchecked")
                List<TimeSheetResponseDto> timeSheetResponseDtoList = (List<TimeSheetResponseDto>) dataList;

                boolean singleUser = timeSheetResponseDtoList.stream()
                        .map(t -> t.getUser() != null ? t.getUser().getId() : null)
                        .distinct()
                        .count() == 1;

                String sheetName = "TimeSheet";
                if (singleUser && timeSheetResponseDtoList.get(0).getUser() != null) {
                    sheetName = "TimeSheet_" + timeSheetResponseDtoList.get(0).getUser().getUsername();
                }
                sheetName = sanitizeSheetName(sheetName);
                Sheet sheet = workbook.createSheet(sheetName);

                int rowIdx = 0;

                if (singleUser && timeSheetResponseDtoList.get(0).getUser() != null) {
                    Row userRow = sheet.createRow(rowIdx++);
                    userRow.createCell(0).setCellValue("User");
                    userRow.createCell(1).setCellValue(timeSheetResponseDtoList.get(0).getUser().getUsername());
                }

                Row header = sheet.createRow(rowIdx++);
                String[] columns = singleUser
                        ? new String[]{"Date","Start","End","Description","Project"}
                        : new String[]{"User","Date","Start","End","Description","Project"};
                for(int i = 0; i < columns.length; i++)
                {
                    header.createCell(i).setCellValue(columns[i]);
                }

                for(TimeSheetResponseDto t : timeSheetResponseDtoList)
                {
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    if (!singleUser) {
                        String u = t.getUser() != null ? t.getUser().getUsername() : "";
                        row.createCell(col++).setCellValue(u);
                    }
                    row.createCell(col++).setCellValue(t.getDate().toString());
                    row.createCell(col++).setCellValue(t.getStartTime().toString());
                    row.createCell(col++).setCellValue(t.getEndTime().toString());
                    row.createCell(col++).setCellValue(t.getDescription());
                    row.createCell(col++).setCellValue(t.getProject().getName());
                }

                for(int i = 0; i < columns.length; i++)
                {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                return out.toByteArray();
            }
            else if(dataList.get(0) instanceof UserResponseDto) //ID,Kullanıcı,Email,Rol
            {
                @SuppressWarnings("unchecked")
                List<UserResponseDto> userResponseDtoList = (List<UserResponseDto>) dataList;

                Sheet sheet = workbook.createSheet("users");

                Row header = sheet.createRow(0);
                String[] columns = {"ID","User","Email","Role","Project"};
                for(int i = 0;i< columns.length;i++)
                {
                    header.createCell(i).setCellValue(columns[i]);
                }

                int rowNum = 1;
                for(UserResponseDto u : userResponseDtoList)
                {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(u.getId().toString());
                    row.createCell(1).setCellValue(u.getUsername());
                    row.createCell(2).setCellValue(u.getEmail());
                    row.createCell(3).setCellValue(u.getRole().toString());
                }

                for(int i = 0;i< columns.length;i++)
                {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                return out.toByteArray();
            }
        }
        return new byte[0];
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

    public List<UserResponseDto> searchUserForExport(String q)
    {
        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        List<User> userList = userRepository.findByUsernameContainsIgnoreCaseOrEmailEquals(q, q, sort);

        List<UserResponseDto> userResponseDtoList = userList.stream()
                .map(element -> modelMapper.map(element, UserResponseDto.class))
                .toList();

        return userResponseDtoList;
    }

    public List<TimeSheetResponseDto> searchTimeSheetForExport(Long userId, LocalDate localDate)
    {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");

        List<TimeSheet> timeSheetList;

        if (userId != null && localDate != null) {
            timeSheetList = timeSheetRepository.findByUser_IdEqualsAndDateEquals(userId, localDate, sort);
        } else if (userId != null) {
            timeSheetList = timeSheetRepository.findByUser_IdEquals(userId, sort);
        } else if (localDate != null) {
            timeSheetList = timeSheetRepository.findByDateEquals(localDate, sort);
        } else {
            timeSheetList = timeSheetRepository.findAll(sort);
        }

        List<TimeSheetResponseDto> timeSheetResponseDtoList = timeSheetList.stream()
                .map(element -> modelMapper.map(element, TimeSheetResponseDto.class))
                .toList();
        return timeSheetResponseDtoList;
    }

    private String sanitizeSheetName(String name) {
        String cleaned = name.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return cleaned.length() > 31 ? cleaned.substring(0, 31) : cleaned;
    }

    @NonNull
    private Page<TimeSheetResponseDto> getTimeSheetResponseDtos(Page<TimeSheet> timeSheetPage) {
        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> {
            TimeSheetResponseDto dto = modelMapper.map(element, TimeSheetResponseDto.class);
            return dto;
        });
        return timeSheetResponseDtoPage;
    }

}
