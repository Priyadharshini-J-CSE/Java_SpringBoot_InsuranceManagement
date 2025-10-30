package com.insurance.service;

import com.insurance.entity.User;
import com.insurance.entity.Wallet;
import com.insurance.repository.UserRepository;
import com.insurance.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Create wallet for user
        Wallet wallet = new Wallet(savedUser);
        walletRepository.save(wallet);
        
        return savedUser;
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        try {
            boolean exists = userRepository.existsByEmail(email);
            System.out.println("Email " + email + " exists: " + exists);
            return exists;
        } catch (Exception e) {
            System.out.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean existsByAadhaarNumber(String aadhaarNumber) {
        return userRepository.existsByAadhaarNumber(aadhaarNumber);
    }
    
    public void enableUser(String email) {
        User user = findByEmail(email);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
    
    public boolean verifyAadhaar(String aadhaarNumber, String fullName) {
        // Simplified Aadhaar verification - in real implementation, integrate with UIDAI API
        return aadhaarNumber.length() == 12 && fullName.length() > 2;
    }
    
    public void verifyUserAadhaar(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setAadhaarVerified(true);
            userRepository.save(user);
        }
    }
    
    public boolean isPasswordStrong(String password) {
        return password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*\\d.*") && 
               password.matches(".*[!@#$%^&*()].*");
    }
}