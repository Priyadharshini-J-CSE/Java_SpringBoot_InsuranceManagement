package com.insurance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
public class ClaimDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "claim_id")
    private Claim claim;
    
    private String documentName;
    private String documentType;
    private String filePath;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;
    
    private Long fileSize;
    private LocalDateTime uploadedDate = LocalDateTime.now();
    
    public ClaimDocument() {}
    
    public ClaimDocument(Claim claim, String documentName, String documentType, String filePath) {
        this.claim = claim;
        this.documentName = documentName;
        this.documentType = documentType;
        this.filePath = filePath;
    }
    
    public ClaimDocument(Claim claim, String documentName, String documentType, byte[] fileData, Long fileSize) {
        this.claim = claim;
        this.documentName = documentName;
        this.documentType = documentType;
        this.fileData = fileData;
        this.fileSize = fileSize;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public LocalDateTime getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDateTime uploadedDate) { this.uploadedDate = uploadedDate; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}