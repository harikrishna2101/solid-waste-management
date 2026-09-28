package com.smartwaste.service;

import com.smartwaste.entity.Complaint;

public interface EmailService {
    void sendComplaintRegistrationEmail(Complaint complaint, String citizenEmail, String adminEmail);
    void sendVerificationEmail(String email, String otp);
}
