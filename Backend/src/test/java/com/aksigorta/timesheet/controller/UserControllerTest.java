package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.user.UserLoginDto;
import com.aksigorta.timesheet.model.user.UserRegisterDto;
import com.aksigorta.timesheet.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void register_shouldDelegateToService() {
        UserRegisterDto dto = new UserRegisterDto("testuser", "test@mail.com", "password123");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(userService).register(dto);

        ResponseEntity<?> result = userController.register(dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userService).register(dto);
    }

    @Test
    void login_shouldDelegateToService() {
        UserLoginDto dto = new UserLoginDto("testuser", "password123");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(userService).login(dto);

        ResponseEntity<?> result = userController.login(dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userService).login(dto);
    }
}