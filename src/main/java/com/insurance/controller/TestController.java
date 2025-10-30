package com.insurance.controller;

import com.insurance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/test-db")
    public String testDatabase() {
        try {
            boolean exists = userService.existsByEmail("test@test.com");
            return "Database connection working! Test email exists: " + exists;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}