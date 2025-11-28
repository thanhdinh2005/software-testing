package com.software.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    /**
     * Username:
     * - Không được null hoặc rỗng (@NotBlank)
     * - Độ dài từ 3 đến 50 ký tự (@Size)
     * - Chỉ cho phép ký tự a-z, A-Z, 0-9, dấu gạch ngang (-), dấu chấm (.), dấu gạch dưới (_)
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 - 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username may only contain letters, digits, hyphen (-), dot (.), and underscore (_)"
    )
    private String username;

    /**
     * Password:
     * - Không được null hoặc rỗng (@NotBlank)
     * - Độ dài từ 6 đến 100 ký tự (@Size)
     * - Phải chứa ít nhất một chữ cái và một chữ số (@Pattern)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 - 100 characters")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,100}$",
            message = "Password must contain at least one letter and one digit"
    )
    private String password;
}

