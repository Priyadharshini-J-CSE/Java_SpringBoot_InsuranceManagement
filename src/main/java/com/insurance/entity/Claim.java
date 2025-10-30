package com.insurance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String claimNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
    
    private String claimType;
    private String description;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    
    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.PENDING;
    
    private LocalDateTime submittedDate = LocalDateTime.now();
    private LocalDateTime processedDate;
    
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL)
    private List<ClaimDocument> claimDocuments;
    
    private String adminComments;
    
    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED, PAID
    }
    
    public Claim() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    
    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }
    
    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }
    
    public List<ClaimDocument> getClaimDocuments() { return claimDocuments; }
    public void setClaimDocuments(List<ClaimDocument> claimDocuments) { this.claimDocuments = claimDocuments; }
    
    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }
}