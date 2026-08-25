package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.model.user.UserLoginDto;
import com.aksigorta.timesheet.model.user.UserRegisterDto;
import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock JwtService jwtService;

    @InjectMocks
    UserService userService;

    @Test
    void register_shouldReturnOk_whenUserDoesNotExist()
    {
        // ARRANGE
        UserRegisterDto dto = new UserRegisterDto("testuser","test@mail.com", "plainPassword");

        when(userRepository.findByMailOrUsername(dto.getEmail(), dto.getUsername()))
                .thenReturn(Optional.empty());

        User mappedUser = new User();
        mappedUser.setEmail(dto.getEmail());
        mappedUser.setUsername(dto.getUsername());
        mappedUser.setPassword(dto.getPassword());


        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setEmail(dto.getEmail());
        responseDto.setUsername(dto.getUsername());
        responseDto.setRole(Role.USER);


        // ACT
        ResponseEntity<?> result = userService.register(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(responseDto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenUserExists()
    {
        // ARRANGE
        UserRegisterDto dto = new UserRegisterDto("testuser", "test@mail.com", "plainPassword");

        when(userRepository.findByMailOrUsername(dto.getEmail(), dto.getUsername()))
                .thenReturn(Optional.of(new User()));

        // ACT
        ResponseEntity<?> result = userService.register(dto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepository).findByMailOrUsername(dto.getEmail(), dto.getUsername());
    }

    @Test
    void register_shouldEncodePassword_beforeSaving()
    {
        // ARRANGE
        UserRegisterDto dto = new UserRegisterDto("testuser", "test@mail.com", "plainPassword");

        when(userRepository.findByMailOrUsername(dto.getEmail(), dto.getUsername()))
                .thenReturn(Optional.empty());

        User mappedUser = new User();
        mappedUser.setPassword(dto.getPassword());

        // ACT
        userService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // ASSERT
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNotEquals(dto.getPassword(), savedUser.getPassword());
    }

    @Test
    void login_shouldReturnToken_whenSuccessfull()
    {
        // ARRANGE
        UserLoginDto userLoginDto = new UserLoginDto("testuser", "plainPassword");

        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setPassword(BCrypt.hashpw("plainPassword", BCrypt.gensalt()));

        when(userRepository.findByUsername(userLoginDto.getUsername()))
                .thenReturn(Optional.of(existingUser));

        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        // ACT
        ResponseEntity<?> result = userService.login(userLoginDto);

        Map<String, Object> responseBody = (Map<String, Object>) result.getBody();

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotEquals(null, responseBody.get("token"));

        verify(jwtService).generateToken(any());
    }

    @Test
    void login_shouldReturnBadRequest_whenUserPasswordIncorrect() {
        // ARRANGE
        UserLoginDto userLoginDto = new UserLoginDto("nonexistentuser", "incorrectPassword");

        User existingUser = new User();
        existingUser.setPassword(BCrypt.hashpw("correctPassword", BCrypt.gensalt()));


        when(userRepository.findByUsername(userLoginDto.getUsername()))
                .thenReturn(Optional.of(existingUser));

        // ACT
        ResponseEntity<?> result = userService.login(userLoginDto);

        // ASSERT
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}