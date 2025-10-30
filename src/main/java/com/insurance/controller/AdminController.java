package com.insurance.controller;

import com.insurance.entity.*;
import com.insurance.service.*;
import com.insurance.repository.ClaimDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private ClaimDocumentRepository claimDocumentRepository;
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Policy> pendingPolicies = policyService.getPendingPolicies();
        List<Claim> pendingClaims = claimService.getPendingClaims();
        List<Policy> allPolicies = policyService.getAllPolicies();
        
        model.addAttribute("pendingPolicies", pendingPolicies);
        model.addAttribute("pendingClaims", pendingClaims);
        model.addAttribute("allPolicies", allPolicies);
        model.addAttribute("pendingPolicyCount", pendingPolicies.size());
        model.addAttribute("pendingClaimCount", pendingClaims.size());
        model.addAttribute("totalPolicyCount", allPolicies.size());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/policies")
    public String managePolicies(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "admin/policies";
    }
    
    @PostMapping("/approve-policy/{id}")
    public String approvePolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        policyService.approvePolicy(id);
        redirectAttributes.addFlashAttribute("message", "Policy approved successfully!");
        return "redirect:/admin/policies";
    }
    
    @PostMapping("/reject-policy/{id}")
    public String rejectPolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        policyService.rejectPolicy(id);
        redirectAttributes.addFlashAttribute("message", "Policy rejected!");
        return "redirect:/admin/policies";
    }
    
    @GetMapping("/claims")
    public String manageClaims(Model model) {
        List<Claim> claims = claimService.getPendingClaims();
        model.addAttribute("claims", claims);
        return "admin/claims";
    }
    
    @PostMapping("/approve-claim/{id}")
    public String approveClaim(@PathVariable Long id, 
                              @RequestParam BigDecimal approvedAmount,
                              RedirectAttributes redirectAttributes) {
        claimService.approveClaim(id, approvedAmount);
        redirectAttributes.addFlashAttribute("message", "Claim approved and amount added to wallet!");
        return "redirect:/admin/claims";
    }
    
    @PostMapping("/reject-claim/{id}")
    public String rejectClaim(@PathVariable Long id, 
                             @RequestParam String reason,
                             RedirectAttributes redirectAttributes) {
        claimService.rejectClaim(id, reason);
        redirectAttributes.addFlashAttribute("message", "Claim rejected!");
        return "redirect:/admin/claims";
    }
    
    @GetMapping("/claim/{id}/documents")
    public String viewClaimDocuments(@PathVariable Long id, Model model) {
        Claim claim = claimService.getClaimById(id);
        if (claim != null) {
            List<ClaimDocument> documents = claimDocumentRepository.findByClaim(claim);
            model.addAttribute("claim", claim);
            model.addAttribute("documents", documents);
        }
        return "admin/claim-documents";
    }
    
    @GetMapping("/document/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            ClaimDocument document = claimDocumentRepository.findById(id).orElse(null);
            if (document == null || document.getFileData() == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getDocumentName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, document.getDocumentType())
                .body(document.getFileData());
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}