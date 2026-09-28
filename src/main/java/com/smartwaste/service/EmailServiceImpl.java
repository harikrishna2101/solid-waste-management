package com.smartwaste.service;

import com.smartwaste.entity.Complaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendComplaintRegistrationEmail(Complaint complaint, String citizenEmail, String adminEmail) {
        try {
            // Send email to Citizen
            SimpleMailMessage citizenMessage = new SimpleMailMessage();
            citizenMessage.setTo(citizenEmail);
            citizenMessage.setSubject("Complaint Registered Successfully - ID: " + complaint.getComplaintId());
            citizenMessage.setText("Dear Citizen,\n\n" +
                    "Your complaint has been successfully registered.\n\n" +
                    "Complaint ID: " + complaint.getComplaintId() + "\n" +
                    "Category: " + complaint.getCategory() + "\n\n" +
                    "You can track the status of your complaint here: http://localhost:9093/user/complaints/" + complaint.getId() + "\n\n" +
                    "Thank you,\nSmart Waste Management System");
            mailSender.send(citizenMessage);

            // Send email to Admin
            if (adminEmail != null && !adminEmail.isEmpty()) {
                SimpleMailMessage adminMessage = new SimpleMailMessage();
                adminMessage.setTo(adminEmail);
                adminMessage.setSubject("New Complaint Registered - ID: " + complaint.getComplaintId());
                adminMessage.setText("Dear Admin,\n\n" +
                        "A new complaint has been registered.\n\n" +
                        "Complaint ID: " + complaint.getComplaintId() + "\n" +
                        "Reported by: " + complaint.getCitizen().getName() + " (" + citizenEmail + ")\n" +
                        "Category: " + complaint.getCategory() + "\n\n" +
                        "Please login to the admin dashboard to take action.\n\n" +
                        "Smart Waste Management System");
                mailSender.send(adminMessage);
            }
        } catch (Exception e) {
            // Log the error but do not throw to prevent the main transaction from failing
            // In a real application, consider using a proper logger.
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendVerificationEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Verify Your Smart Waste Management Account");
            message.setText("Welcome to Smart Waste Management!\n\n" +
                    "Your email verification code is: " + otp + "\n\n" +
                    "To activate your account instantly, please click the link below:\n" +
                    "http://localhost:9093/verify?email=" + email + "&otp=" + otp + "\n\n" +
                    "Thank you,\nSmart Waste Management Team");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
}
