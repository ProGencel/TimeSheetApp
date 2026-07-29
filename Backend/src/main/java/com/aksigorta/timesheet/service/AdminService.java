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

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

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
}
