package com.software.backend.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // -------------------------------
    // SUCCESS CASE
    // -------------------------------
    @Test
  @DisplayName("Login successfully - returns 200 OK with LoginResponse")

    void login_success() throws Exception {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Success");
        loginResponse.setToken("jwt-token");
        loginResponse.setIsSuccess(true);

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.isSuccess").value(true))
                .andExpect(jsonPath("$.data.message").value("Success"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));

                verify(authService, times(1)).authenticate(any());

    }

    // -------------------------------
    // FAILURE CASE
    // -------------------------------
    @Test
@DisplayName("Login failed - throws BadRequestException")
    void login_failure_badRequest() throws Exception {
        

        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("wrong123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is required"));
   
    }
    @Test
@DisplayName("Login failed - username contains invalid characters")
void login_invalidUsername_badCharacters() throws Exception {
    // Username chứa ký tự không hợp lệ (@) → vi phạm regex
    LoginRequest request = new LoginRequest("lai@12333", "password123");


    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Username may only contain letters, digits, hyphen (-), dot (.), and underscore (_)"));

    // Không gọi service nếu dữ liệu không hợp lệ
    verify(authService, times(0)).authenticate(any());
}

@Test
@DisplayName("Login failed - username too short")
void login_invalidUsername_tooShort() throws Exception {
    // Username chỉ có 2 ký tự → vi phạm @Size(min = 3)
    LoginRequest request = new LoginRequest();
    request.setUsername("ab");
    request.setPassword("password123");

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Username must be between 3 - 50 characters"));

    verify(authService, times(0)).authenticate(any());
}

@Test
@DisplayName("Login failed - password missing digit")
void login_invalidPassword_missingDigit() throws Exception {
    // Password không chứa số → vi phạm regex
    LoginRequest request = new LoginRequest();
    request.setUsername("user123");
    request.setPassword("abcdef");

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Password must contain at least one letter and one digit"));

    verify(authService, times(0)).authenticate(any());
}

@Test
@DisplayName("Login failed - password too short")
void login_invalidPassword_tooShort() throws Exception {
    // Password chỉ có 3 ký tự → vi phạm @Size(min = 6)
    LoginRequest request = new LoginRequest();
    request.setUsername("user123");
    request.setPassword("a1b@");

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Password must be between 6 - 100 characters"));

    verify(authService, times(0)).authenticate(any());
}
}
