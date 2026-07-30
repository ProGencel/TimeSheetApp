package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.TimeSheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TimeSheetRepository timeSheetRepository;
    private final UserRepository userRepository;
    private final UserService userService;
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
        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);

        Page<TimeSheet> timeSheetPage;

        if (localDate != null) {
            timeSheetPage = timeSheetRepository.findByUser_IdEqualsAndDateEquals(userId, localDate, pageable);
        } else {
            timeSheetPage = timeSheetRepository.findByUser_IdEquals(userId, pageable);
        }

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return ResponseEntity.ok().body(timeSheetResponseDtoPage);

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

            String username = timeSheetList.get(0).getUser().getUsername();

            sb.append("Kullanıcı,").append(escapeCsv(username)).append("\n");

            sb.append("Tarih,Başlangıç,Bitiş,Açıklama\n");

            for(TimeSheetResponseDto t : timeSheetList)
            {
                sb.append(t.getDate()).append(",")
                        .append(t.getStartTime()).append(",")
                        .append(t.getEndTime()).append(",")
                        .append(escapeCsv(t.getDescription()))
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

        if (localDate != null) {
            timeSheetList = timeSheetRepository.findByUser_IdEqualsAndDateEquals(userId, localDate, sort);
        } else {
            timeSheetList = timeSheetRepository.findByUser_IdEquals(userId, sort);
        }

        List<TimeSheetResponseDto> timeSheetResponseDtoList = timeSheetList.stream()
                .map(element -> modelMapper.map(element, TimeSheetResponseDto.class))
                .toList();
        return timeSheetResponseDtoList;
    }

}
