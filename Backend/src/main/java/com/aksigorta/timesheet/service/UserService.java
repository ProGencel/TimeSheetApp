package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.model.UserRegisterDto;
import com.aksigorta.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<?> register(UserRegisterDto userRegisterDto)
    {
        Optional<User> userOptional = userRepository.findByMailOrUsername(userRegisterDto.getEmail(),userRegisterDto.getUsername());

        if(userOptional.isEmpty())
        {
            User user = modelMapper.map(userRegisterDto,User.class);
            userRepository.save(user);

            return ResponseEntity.ok().body(user);
        }
        Map<String, Object> errorMessage = Map.of("Success", false, "error message:", "Your email or username is already in use !");
        return ResponseEntity.badRequest().body(errorMessage);
    }

}