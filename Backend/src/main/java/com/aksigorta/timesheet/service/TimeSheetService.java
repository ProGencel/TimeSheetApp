package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetSaveDto;
import com.aksigorta.timesheet.model.user.User;
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
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<?> save(TimeSheetSaveDto timeSheetSaveDto)
    {
        // TODO: add JWT authentication
        Long userId = 1L;

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
        Long userid = 1L;

        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<TimeSheet> timeSheetPage = timeSheetRepository.findByUserIdEquals(userid,pageable);

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return timeSheetResponseDtoPage;
    }

    public Page<TimeSheetResponseDto> searchTimeSheets(int page, LocalDate startDate, LocalDate endDate)
    {
        // TODO: add JWT authentication
        Long userId = 1L;

        Sort sort = Sort.by(Sort.Direction.DESC,"date");
        Pageable pageable = PageRequest.of(page,10,sort);
        Page<TimeSheet> timeSheetPage = timeSheetRepository.
                findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(userId,startDate,endDate,pageable);

        Page<TimeSheetResponseDto> timeSheetResponseDtoPage = timeSheetPage.map((element) -> modelMapper.map(element, TimeSheetResponseDto.class));

        return timeSheetResponseDtoPage;
    }

}
