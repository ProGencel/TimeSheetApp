package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserLoginDto;
import com.aksigorta.timesheet.model.user.UserRegisterDto;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.UserRepository;
import com.aksigorta.timesheet.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<?> register(UserRegisterDto userRegisterDto)
    {
        Optional<User> userOptional = userRepository.findByMailOrUsername(userRegisterDto.getEmail(),userRegisterDto.getUsername());

        if(userOptional.isEmpty())
        {
            User user = modelMapper.map(userRegisterDto,User.class);
            String hashPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashPassword);

            userRepository.save(user);

            UserResponseDto userResponseDto = modelMapper.map(user,UserResponseDto.class);
            return ResponseEntity.ok().body(userResponseDto);
        }
        Map<String, Object> errorMessage = Map.of("Success", false, "error message:", "Your email or username is already in use !");
        return ResponseEntity.badRequest().body(errorMessage);
    }

    public ResponseEntity<?> login(UserLoginDto userLoginDto)
    {
        Optional<User> userOptional = userRepository.findByMail(userLoginDto.getUsername());
        Map<String, Object> responseBody = new HashMap<>();

        if(userOptional.isPresent())
        {
            User user = userOptional.get();
            boolean isPasswordMatch = BCrypt.checkpw(userLoginDto.getPassword(), user.getPassword());
            if(isPasswordMatch)
            {
                CustomUserDetails customUserDetails = new CustomUserDetails(user);
                String token = jwtService.generateToken(customUserDetails);
                responseBody.put("token",token);

                UserResponseDto userResponseDto = modelMapper.map(user,UserResponseDto.class);

                responseBody.put("user", userResponseDto);

                return ResponseEntity.ok().body(responseBody);
            }
        }
        responseBody.put("success", false);
        responseBody.put("error message:", "Incorrect username or password");
        return ResponseEntity.badRequest().body(responseBody);
    }

}