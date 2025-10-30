package com.insurance.config;

import com.insurance.entity.User;
import com.insurance.repository.UserRepository;
import com.insurance.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private WalletService walletService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Create test user if not exists
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setPassword(passwordEncoder.encode("test123"));
                testUser.setEmail("test@test.com");
                testUser.setFullName("Test User");
                testUser.setAadhaarNumber("123456789012");
                testUser.setRole(User.Role.USER);
                testUser.setEnabled(true);
                
                User savedUser = userRepository.save(testUser);
                walletService.createWallet(savedUser);
                System.out.println("Test user created: testuser/test123");
            }
            
            // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setEmail("admin@admin.com");
                adminUser.setFullName("Admin User");
                adminUser.setAadhaarNumber("999999999999");
                adminUser.setRole(User.Role.ADMIN);
                adminUser.setEnabled(true);
                
                User savedAdmin = userRepository.save(adminUser);
                walletService.createWallet(savedAdmin);
                System.out.println("Admin user created: admin/admin123");
            }
        } catch (Exception e) {
            System.out.println("Error creating users: " + e.getMessage());
        }
    }
}