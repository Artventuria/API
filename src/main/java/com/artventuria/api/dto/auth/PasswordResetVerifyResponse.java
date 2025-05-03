package com.artventuria.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetVerifyResponse {
    public PasswordResetVerifyResponse(boolean valid) {
        this.valid = valid;
    }
    private boolean valid;
    private String message;
}