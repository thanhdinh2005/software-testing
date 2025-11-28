package com.software.backend.service;

import com.software.backend.dto.request.LoginRequest;
import com.software.backend.dto.response.LoginResponse;
import com.software.backend.entity.User;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public LoginResponse authenticate(LoginRequest request) throws BadRequestException {
        validateLoginRequest(request);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Wrong password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return LoginResponse.builder()
                .isSuccess(true)
                .message("Success")
                .token(token)
                .build();
    }

    private void validateLoginRequest(LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getUsername() == null) {
            throw new IllegalArgumentException("Username is required");
        }

        String username = request.getUsername().trim();

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 - 50 characters");
        }

        // Validate password
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Password is required");
        }

        String password = request.getPassword().trim();

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (password.length() < 6 || password.length() > 100) {
            throw new IllegalArgumentException("Password must be between 6 - 100 characters");
        }

        // ✅ Ràng buộc thêm cho username: chỉ chứa a-z, A-Z, 0-9, -, ., _
        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("Username may only contain letters, digits, hyphen (-), dot (.), and underscore (_)");
        }

        // ✅ Ràng buộc thêm cho password: phải chứa ít nhất một chữ và một số
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,100}$")) {
            throw new IllegalArgumentException("Password must contain at least one letter and one digit");
        }
    }
}