package com.artventuria.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirmRequest {
    @NotBlank
    private String token;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9a-zA-Z@#$%^&+=!]).{8,}$", message = "Password must not contain spaces or special characters other than @#$%^&+=!")
    private String newPassword;
}