package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenFound() {
        User user = new User();
        user.setEmail("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailService.loadUserByUsername("test@mail.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@mail.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenNotFound() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailService.loadUserByUsername("unknown@mail.com"));
    }
}