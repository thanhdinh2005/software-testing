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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {
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
//Cau A: Test method authenticate() với các scenarios
     @Test
    @DisplayName("TC1: Dang nhap thanh cong")
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
       
}
@Test
    @DisplayName("TC2: Dang nhap that bai - Khong tim thay nguoi dung")
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
    @DisplayName("TC3: Dang nhap that bai - Sai mat khau")
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
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_NullRequest() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(null)
        );
        assertEquals("Request cannot be null", ex.getMessage());
    }

// Cau B: Test validation methods riêng lẻ
    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_NullLoginRequest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(null);
        });
        assertEquals("Request cannot be null", exception.getMessage());
    }
    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_UsernameIsRequired() {
        LoginRequest request = LoginRequest.builder()
                .username(null)
                .password("validPassword123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_UsernameIsNotBlank() {
        LoginRequest request = LoginRequest.builder()
                .username("")
                .password("lai@123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Username is not blank", exception.getMessage());
    }

    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_UsernameLengthConstraints() {
        LoginRequest request = LoginRequest.builder()
                .username("a")
                .password("lai@123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Username must be between 3 - 50 characters", exception.getMessage());
    }

    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_UsernameLengthExceedsMaximum() {
        //tao ra chuoi dai hon 50 ky tu
        String longUsername = "a".repeat(51);
        LoginRequest request = LoginRequest.builder()
                .username(longUsername)
                .password("validPassword123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Username must be between 3 - 50 characters", exception.getMessage());
    }

    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_PasswordIsRequired() {
        LoginRequest request = LoginRequest.builder()
                .username("validUsername")
                .password(null)
                .build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Password is required", exception.getMessage());
    
    }
    @Test
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_PasswordIsNotBlank() {
        LoginRequest request = LoginRequest.builder()
                .username("validUsername")
                .password("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Password is not blank", exception.getMessage());
    }

    @Test 
    @DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_PasswordLengthConstraints() {
        LoginRequest request = LoginRequest.builder().username("Laiii").password("12").build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Password must be between 6 - 100 characters", exception.getMessage());
    }

@Test 
@DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_PasswordLengthExceedsMaximum() {
        String longPassword = "a".repeat(101);
        LoginRequest request = LoginRequest.builder()
                .username("validUsername")
                .password(longPassword)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(request);
        });
        assertEquals("Password must be between 6 - 100 characters", exception.getMessage());
    }

@Test
@DisplayName("TC4: Dang nhap that bai - Loi validate")
void validate_UsernamenotcontainInvalidCharacters() {
    LoginRequest request = LoginRequest.builder()
            .username("invalid*user")
            .password("validPassword123")
            .build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        authService.authenticate(request);
    });
    assertEquals("Username may only contain letters, digits, hyphen (-), dot (.), and underscore (_)", exception.getMessage());
}
@Test
@DisplayName("TC4: Dang nhap that bai - Loi validate")
    void validate_PasswordWithValidCharacters() {
    LoginRequest request = LoginRequest.builder()
            .username("validUser")
            .password("Valid_")
            .build();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        authService.authenticate(request);
    }); 
    assertEquals("Password must contain at least one letter and one digit", exception.getMessage());
}

    



}
