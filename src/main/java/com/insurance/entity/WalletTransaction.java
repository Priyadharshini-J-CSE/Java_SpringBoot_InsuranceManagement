package com.insurance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    private BigDecimal amount;
    private String description;
    private String referenceId;
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    public enum TransactionType {
        CREDIT, DEBIT
    }
    
    public WalletTransaction() {}
    
    public WalletTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description, String referenceId) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.referenceId = referenceId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}