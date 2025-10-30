package com.insurance.service;

import com.insurance.entity.*;
import com.insurance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private ClaimDocumentRepository claimDocumentRepository;
    
    public Claim submitClaim(Claim claim, List<MultipartFile> documents) throws IOException {
        claim.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        Claim savedClaim = claimRepository.save(claim);
        
        // Save documents
        System.out.println("Processing documents - Count: " + (documents != null ? documents.size() : 0));
        if (documents != null && !documents.isEmpty()) {
            for (MultipartFile file : documents) {
                if (file != null && !file.isEmpty()) {
                    try {
                        System.out.println("Saving document: " + file.getOriginalFilename() + ", Size: " + file.getSize());
                        String fileName = saveDocument(file, savedClaim.getId());
                        ClaimDocument document = new ClaimDocument(
                            savedClaim, 
                            file.getOriginalFilename(), 
                            file.getContentType(), 
                            fileName
                        );
                        ClaimDocument savedDoc = claimDocumentRepository.save(document);
                        System.out.println("Document saved with ID: " + savedDoc.getId());
                    } catch (Exception e) {
                        System.out.println("Error saving document: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Skipping empty file");
                }
            }
        } else {
            System.out.println("No documents to process");
        }
        

        
        return savedClaim;
    }
    
    public List<Claim> getUserClaims(User user) {
        return claimRepository.findByUserOrderBySubmittedDateDesc(user);
    }
    
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus(Claim.ClaimStatus.PENDING);
    }
    
    public void approveClaim(Long claimId, BigDecimal approvedAmount) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim != null) {
            claim.setStatus(Claim.ClaimStatus.APPROVED);
            claim.setApprovedAmount(approvedAmount);
            claim.setProcessedDate(LocalDateTime.now());
            claimRepository.save(claim);
            
            // Add to wallet
            walletService.addToWallet(claim.getUser(), approvedAmount, "Claim Settlement: " + claim.getClaimNumber());
            
            // Send notification
            Notification notification = new Notification(
                claim.getUser(),
                "Claim Approved",
                "Your claim " + claim.getClaimNumber() + " has been approved for ₹" + approvedAmount
            );
            notificationRepository.save(notification);
        }
    }
    
    public void rejectClaim(Long claimId, String reason) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim != null) {
            claim.setStatus(Claim.ClaimStatus.REJECTED);
            claim.setAdminComments(reason);
            claim.setProcessedDate(LocalDateTime.now());
            claimRepository.save(claim);
            
            // Send notification
            Notification notification = new Notification(
                claim.getUser(),
                "Claim Rejected",
                "Your claim " + claim.getClaimNumber() + " has been rejected. Reason: " + reason
            );
            notificationRepository.save(notification);
        }
    }
    
    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElse(null);
    }
    
    private String saveDocument(MultipartFile file, Long claimId) throws IOException {
        String uploadDir = "uploads/claims/" + claimId + "/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;
        file.transferTo(new File(filePath));
        
        return filePath;
    }
}