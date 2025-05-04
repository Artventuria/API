package com.artventuria.api.service.email;

public interface EmailService {
    /**
     * Sends a password reset email to the specified email address
     * 
     * @param to The recipient's email address
     * @param resetToken The password reset token
     * @throws Exception if the email fails to send
     */
    void sendPasswordResetEmail(String to, String resetToken) throws Exception;
}