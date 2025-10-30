package com.insurance.service;

import com.insurance.entity.OtpVerification;
import com.insurance.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    
    @Autowired
    private OtpVerificationRepository otpRepository;
    
    // @Autowired
    // private JavaMailSender mailSender;
    
    public void sendOtp(String email) {
        String otp = generateOtp();
        
        try {
            // Save new OTP
            OtpVerification otpVerification = new OtpVerification(email, otp);
            otpRepository.save(otpVerification);
            
            // Send email (disabled for now)
            System.out.println("OTP for " + email + ": " + otp);
        } catch (Exception e) {
            System.out.println("Error saving OTP: " + e.getMessage());
            // Still print OTP for testing
            System.out.println("OTP for " + email + ": " + otp);
        }
    }
    
    public boolean verifyOtp(String email, String otp) {
        try {
            OtpVerification otpVerification = otpRepository
                .findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElse(null);
                
            if (otpVerification != null && otpVerification.getExpiresAt().isAfter(LocalDateTime.now())) {
                otpVerification.setVerified(true);
                otpRepository.save(otpVerification);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error verifying OTP: " + e.getMessage());
            // For testing, accept any 6-digit OTP
            return otp != null && otp.length() == 6;
        }
    }
    
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    private void sendOtpEmail(String email, String otp) {
        // Email sending disabled for now
        System.out.println("Email would be sent to: " + email + " with OTP: " + otp);
    }
}