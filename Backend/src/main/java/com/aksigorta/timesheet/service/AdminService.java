package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import com.aksigorta.timesheet.model.timeSheet.TimeSheetResponseDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

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

}
