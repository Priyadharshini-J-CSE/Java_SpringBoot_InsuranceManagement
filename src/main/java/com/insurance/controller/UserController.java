package com.insurance.controller;

import com.insurance.entity.*;
import com.insurance.service.*;
import com.insurance.repository.NotificationRepository;
import com.insurance.repository.ClaimDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ClaimDocumentRepository claimDocumentRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Get current user from session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            // Try to get test user as fallback
            currentUser = userService.findByUsername("testuser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            session.setAttribute("currentUser", currentUser);
        }
        
        // Get policies from database
        java.util.List<Policy> userPolicies = policyService.getUserPolicies(currentUser);
        
        // Get claims from database
        java.util.List<Claim> userClaims = claimService.getUserClaims(currentUser);
        
        // Get wallet from database
        Wallet userWallet = walletService.getUserWallet(currentUser);
        if (userWallet == null) {
            userWallet = walletService.createWallet(currentUser);
        }
        
        // Get notifications from database
        java.util.List<Notification> userNotifications = notificationRepository.findByUserOrderByCreatedAtDesc(currentUser);
        System.out.println("Dashboard: Found " + userNotifications.size() + " notifications for user " + currentUser.getUsername());
        
        model.addAttribute("user", currentUser);
        model.addAttribute("policies", userPolicies);
        model.addAttribute("claims", userClaims);
        model.addAttribute("wallet", userWallet);
        model.addAttribute("notifications", userNotifications);
        
        return "user/dashboard";
    }
    
    @GetMapping("/apply-policy")
    public String showApplyPolicyForm(Model model, HttpSession session) {
        // Check if user is logged in
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            currentUser = userService.findByUsername("testuser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            session.setAttribute("currentUser", currentUser);
        }
        
        model.addAttribute("policy", new Policy());
        return "user/apply-policy";
    }
    
    @PostMapping("/apply-policy")
    public String applyPolicy(@ModelAttribute Policy policy, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        try {
            // Get current user from session
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Please login first.");
                return "redirect:/login";
            }
            
            policy.setUser(currentUser);
            policy.setStatus(Policy.PolicyStatus.ACTIVE);
            policy.setAppliedDate(java.time.LocalDateTime.now());
            
            // Save policy to database
            Policy savedPolicy = policyService.applyForPolicy(policy);
            
            redirectAttributes.addFlashAttribute("message", "Policy application submitted successfully! Policy Number: " + savedPolicy.getPolicyNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting policy: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @GetMapping("/submit-claim")
    public String showSubmitClaimForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        System.out.println("Loading submit-claim for user: " + currentUser.getUsername());
        java.util.List<Policy> userPolicies = policyService.getUserPolicies(currentUser);
        System.out.println("Found " + userPolicies.size() + " policies for submit-claim page");
        
        model.addAttribute("claim", new Claim());
        model.addAttribute("policies", userPolicies);
        return "user/submit-claim";
    }
    
    @PostMapping("/submit-claim")
    public String submitClaim(@ModelAttribute Claim claim,
                             @RequestParam("policyId") String policyId,
                             @RequestParam(value = "documents", required = false) MultipartFile[] documents,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("=== CLAIM SUBMISSION DEBUG ===");
            System.out.println("Policy ID: " + policyId);
            System.out.println("Documents array: " + (documents != null ? documents.length : "null"));
            
            if (documents != null) {
                for (int i = 0; i < documents.length; i++) {
                    MultipartFile file = documents[i];
                    if (file != null) {
                        System.out.println("File " + i + ": Name=" + file.getOriginalFilename() + ", Size=" + file.getSize() + ", Empty=" + file.isEmpty());
                    } else {
                        System.out.println("File " + i + ": null");
                    }
                }
            } else {
                System.out.println("Documents array is null");
            }
            
            // Get current user from session
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Please login first.");
                return "redirect:/login";
            }
            
            System.out.println("Current user: " + currentUser.getUsername());
            
            // Get policies from database to find the selected policy
            java.util.List<Policy> userPolicies = policyService.getUserPolicies(currentUser);
            System.out.println("User has " + userPolicies.size() + " policies");
            
            Policy selectedPolicy = userPolicies.stream()
                .filter(p -> p.getPolicyNumber().equals(policyId))
                .findFirst()
                .orElse(null);
            
            if (selectedPolicy != null) {
                System.out.println("Found policy: " + selectedPolicy.getPolicyNumber());
                System.out.println("Claim details - Type: " + claim.getClaimType() + ", Amount: " + claim.getClaimAmount());
                
                // Set claim details
                claim.setUser(currentUser);
                claim.setPolicy(selectedPolicy);
                claim.setStatus(Claim.ClaimStatus.PENDING);
                claim.setSubmittedDate(java.time.LocalDateTime.now());
                
                // Process documents
                java.util.List<MultipartFile> documentList = new java.util.ArrayList<>();
                if (documents != null) {
                    for (MultipartFile file : documents) {
                        if (file != null && !file.isEmpty()) {
                            documentList.add(file);
                            System.out.println("Controller: Adding file - " + file.getOriginalFilename() + ", Size: " + file.getSize());
                        }
                    }
                }
                System.out.println("Controller: Total files to process: " + documentList.size());
                
                // Test: Create a dummy document if no files uploaded
                if (documentList.isEmpty()) {
                    System.out.println("No files uploaded - this is normal if user didn't select any files");
                }
                
                // Save claim to database first
                Claim savedClaim = claimService.submitClaim(claim, null);
                
                // Process documents and save to database
                if (!documentList.isEmpty()) {
                    for (MultipartFile file : documentList) {
                        try {
                            ClaimDocument document = new ClaimDocument(
                                savedClaim,
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes(),
                                file.getSize()
                            );
                            claimDocumentRepository.save(document);
                            System.out.println("Document saved to database: " + file.getOriginalFilename());
                        } catch (Exception e) {
                            System.out.println("Error saving document: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Claim saved with number: " + savedClaim.getClaimNumber());
                
                // Create notification
                Notification notification = new Notification(
                    currentUser,
                    "Claim Submitted",
                    "Your claim " + savedClaim.getClaimNumber() + " has been submitted successfully and is under review."
                );
                Notification savedNotification = notificationRepository.save(notification);
                System.out.println("Notification saved with ID: " + savedNotification.getId());
                
                redirectAttributes.addFlashAttribute("message", "Claim submitted successfully! Claim Number: " + savedClaim.getClaimNumber());
                System.out.println("SUCCESS: Redirecting to dashboard with message");
            } else {
                System.out.println("Policy not found for ID: " + policyId);
                redirectAttributes.addFlashAttribute("error", "Selected policy not found!");
            }
        } catch (Exception e) {
            System.out.println("Error submitting claim: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error submitting claim: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @GetMapping("/wallet")
    public String showWallet(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Wallet userWallet = walletService.getUserWallet(currentUser);
        if (userWallet == null) {
            userWallet = walletService.createWallet(currentUser);
        }
        
        java.util.List<Payment> recentPayments = paymentService.getUserPayments(currentUser);
        java.util.List<Policy> userPolicies = policyService.getUserPolicies(currentUser);
        
        model.addAttribute("wallet", userWallet);
        model.addAttribute("payments", recentPayments);
        model.addAttribute("policies", userPolicies);
        return "user/wallet";
    }
    
    @PostMapping("/wallet/recharge")
    public String rechargeWallet(@RequestParam BigDecimal amount,
                                @RequestParam String accountNumber,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            Payment payment = paymentService.rechargeWallet(currentUser, amount, accountNumber);
            redirectAttributes.addFlashAttribute("message", "Wallet recharged successfully! Transaction ID: " + payment.getTransactionId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Recharge failed: " + e.getMessage());
        }
        
        return "redirect:/wallet";
    }
    
    @PostMapping("/wallet/pay-premium")
    public String payPremium(@RequestParam Long policyId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // Get policy
            Policy policy = policyService.getPolicyById(policyId);
            if (policy == null || !policy.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Policy not found!");
                return "redirect:/wallet";
            }
            
            // Check wallet balance
            Wallet userWallet = walletService.getUserWallet(currentUser);
            if (userWallet == null || userWallet.getBalance().compareTo(policy.getPremiumAmount()) < 0) {
                redirectAttributes.addFlashAttribute("error", "Insufficient wallet balance!");
                return "redirect:/wallet";
            }
            
            // Deduct amount from wallet
            BigDecimal newBalance = userWallet.getBalance().subtract(policy.getPremiumAmount());
            userWallet.setBalance(newBalance);
            userWallet.setLastUpdated(java.time.LocalDateTime.now());
            walletService.updateWallet(userWallet);
            
            // Create payment record
            Payment payment = paymentService.payPremium(currentUser, policy, policy.getPremiumAmount());
            
            // Store payment in session for receipt
            session.setAttribute("lastPayment", payment);
            
            return "redirect:/payment-receipt";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + e.getMessage());
        }
        
        return "redirect:/wallet";
    }
    
    @GetMapping("/payment-receipt")
    public String showPaymentReceipt(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Payment payment = (Payment) session.getAttribute("lastPayment");
        if (payment == null) {
            return "redirect:/wallet";
        }
        
        model.addAttribute("payment", payment);
        model.addAttribute("user", currentUser);
        return "user/payment-receipt";
    }
    
    @GetMapping("/notifications")
    public String showNotifications(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        java.util.List<Notification> userNotifications = notificationRepository.findByUserOrderByCreatedAtDesc(currentUser);
        model.addAttribute("notifications", userNotifications);
        return "user/notifications";
    }
    
    @GetMapping("/payment-history")
    public String showPaymentHistory(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        java.util.List<Payment> userPayments = paymentService.getUserPayments(currentUser);
        model.addAttribute("payments", userPayments);
        return "user/payment-history";
    }
    

}