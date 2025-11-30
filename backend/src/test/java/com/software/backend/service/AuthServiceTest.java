package com.software.backend.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.entity.User;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.repository.UserRepository;
import com.software.backend.dto.response.LoginResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private final String USERNAME = "testuser";
    private final String RAW_PASSWORD = "rawpassword123";
    private final String ENCODED_PASSWORD = "encoded_hash_of_password";
    private final String EXPECTED_TOKEN = "mocked_jwt_token_12345";
    private User user;
    private LoginRequest validRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();

        validRequest = LoginRequest.builder()
                .username(USERNAME)
                .password(RAW_PASSWORD)
                .build();
    }

    @Test
    @DisplayName("TC1: Authentication Success - Valid Username and Password")
    void testAuthenticate_Success() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(USERNAME)).thenReturn(EXPECTED_TOKEN);

        LoginResponse response = authService.authenticate(validRequest);

        assertNotNull(response);
        assertTrue(response.getIsSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(EXPECTED_TOKEN, response.getToken());

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService, times(1)).generateToken(USERNAME);
    }

    @Test
    @DisplayName("TC2: Authentication Failure - User Not Found")
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            authService.authenticate(validRequest);
        });

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(0)).matches(anyString(), anyString());
        verify(jwtService, times(0)).generateToken(anyString());
    }

    @Test
    @DisplayName("TC3: Authentication Failure - Wrong Password")
    void testAuthenticate_WrongPassword() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);
        assertThrows(BadRequestException.class, () -> {
            authService.authenticate(validRequest);
        });

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService, times(0)).generateToken(anyString());
    }

    @Test
    void validate_nullRequest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(null)
        );
    }

    @Test
    void validate_emptyUsername() {
        LoginRequest request = new LoginRequest("  ", "123456");

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );
    }

    @Test
    void validate_invalidPasswordLength() {
        LoginRequest request = new LoginRequest("john", "123");

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );
    }

    @Test
    void validate_invalidUsernameLength() {
        LoginRequest request = new LoginRequest("ab", "123456");

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(request)
        );

        assertTrue(exception.getMessage().contains("Username must be between"));
    }

}
