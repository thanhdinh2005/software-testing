package com.software.backend.controller;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TC1: Dang nhap thanh cong - Happy Path")
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
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.isSuccess").value(true))
                .andExpect(jsonPath("$.data.message").value("Success"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));

                verify(authService, times(1)).authenticate(any());

    }
    @Test
@DisplayName("TC2: Dang nhap that bai - Username is blank")
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
    @DisplayName("TC3: Dang nhap that bai - Username chua ky tu khong hop le")
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
@DisplayName("TC4: Dang nhap that bai - Username qua dai")
void login_invalidUsername_tooLong() throws Exception {
    // Username có độ dài vượt quá 50 ký tự → vi phạm @Size(max = 50)
    LoginRequest request = new LoginRequest();
    request.setUsername("a".repeat(51)); // Tạo chuỗi 51 ký tự 'a'
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
@DisplayName("TC5: Dang nhap that bai - Username qua ngan")
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
@DisplayName("TC6: Dang nhap that bai - Mat khau thieu ky so")
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
@DisplayName("TC7: Dang nhap that bai - Mat khau qua ngan")
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
@Test
@DisplayName("TC8: Dang nhap that bai - Mat khau qua dai")
void login_invalidPassword_tooLong() throws Exception {
    // Password có độ dài vượt quá 100 ký tự → vi phạm @Size(max = 100)
    LoginRequest request = new LoginRequest();
    request.setUsername("user123");
    request.setPassword("a1".repeat(51)); // Tạo chuỗi 102 ký tự

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Password must be between 6 - 100 characters"));

    verify(authService, times(0)).authenticate(any());
}
@Test
@DisplayName("TC9: Dang nhap that bai - Mat khau thieu chu cai")
void login_invalidPassword_missingLetter() throws Exception {
    // Password không chứa chữ cái → vi phạm regex
    LoginRequest request = new LoginRequest();
    request.setUsername("user123");
    request.setPassword("123456");

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Password must contain at least one letter and one digit"));

    verify(authService, times(0)).authenticate(any());
}
@Test
@DisplayName("CORS - Allow Origin header")
void testCorsHeaders() throws Exception {
    mockMvc.perform(post("/api/auth/login")
                    .header("Origin", "http://localhost:3000")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(new LoginRequest("user1", "password123"))))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
}


}
