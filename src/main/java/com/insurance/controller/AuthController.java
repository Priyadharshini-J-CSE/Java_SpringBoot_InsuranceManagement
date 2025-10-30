package com.insurance.controller;

import com.insurance.entity.User;
import com.insurance.service.UserService;
import com.insurance.service.OtpService;
import com.insurance.service.WalletService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        
        try {
            System.out.println("Registration started for: " + user.getEmail());
            
            // Basic validation
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }
            
            // Check if user exists
            if (userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/register";
            }
            
            // Send OTP
            System.out.println("Sending OTP to: " + user.getEmail());
            otpService.sendOtp(user.getEmail());
            
            // Store user in session
            session.setAttribute("tempUser", user);
            System.out.println("User stored in session, redirecting to OTP page");
            
            return "redirect:/verify-otp?email=" + user.getEmail();
            
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/verify-otp")
    public String showOtpForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }
    
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, 
                           @RequestParam String otp,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("OTP verification for: " + email + " with OTP: " + otp);
            
            if (otpService.verifyOtp(email, otp)) {
                // Get user from session
                User tempUser = (User) session.getAttribute("tempUser");
                System.out.println("User from session: " + (tempUser != null ? tempUser.getEmail() : "null"));
                
                if (tempUser != null) {
                    // Register user
                    System.out.println("Registering user: " + tempUser.getEmail());
                    User registeredUser = userService.registerUser(tempUser);
                    userService.enableUser(email);
                    
                    // Create wallet for new user (if not exists)
                    walletService.createWallet(registeredUser);
                    
                    // Clear session
                    session.removeAttribute("tempUser");
                    
                    redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
                    System.out.println("Registration completed successfully");
                    return "redirect:/login";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Session expired. Please register again.");
                    return "redirect:/register";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP");
                return "redirect:/verify-otp?email=" + email;
            }
        } catch (Exception e) {
            System.out.println("OTP verification error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Verification failed: " + e.getMessage());
            return "redirect:/verify-otp?email=" + email;
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("Login attempt for: " + username);
            
            // Find user and validate password
            User user = userService.findByUsername(username);
            if (user != null && user.isEnabled() && passwordEncoder.matches(password, user.getPassword())) {
                // Store user in session
                session.setAttribute("currentUser", user);
                System.out.println("Login successful for user: " + user.getUsername());
                
                if (user.getRole() == User.Role.ADMIN) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/dashboard";
                }
            } else {
                System.out.println("Login failed for username: " + username);
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login";
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            return "redirect:/login";
        }
    }
}