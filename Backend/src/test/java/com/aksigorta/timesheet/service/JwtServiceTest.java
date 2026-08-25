package com.aksigorta.timesheet.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "test_icin_kullanilacak_en_az_32_karakterlik_bir_secret_key");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 milisaniye

        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(10); // token'ın süresinin dolmasını bekle

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
}