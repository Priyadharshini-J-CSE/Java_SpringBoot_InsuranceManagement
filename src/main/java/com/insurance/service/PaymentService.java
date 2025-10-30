package com.insurance.service;

import com.insurance.entity.*;
import com.insurance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private WalletService walletService;
    
    public Payment rechargeWallet(User user, BigDecimal amount, String accountNumber) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentType(Payment.PaymentType.RECHARGE);
        payment.setAccountNumber(accountNumber);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setDescription("Wallet Recharge from Account: " + accountNumber);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Add amount to wallet
        walletService.addToWallet(user, amount, "Wallet Recharge - " + savedPayment.getTransactionId());
        
        return savedPayment;
    }
    
    public Payment payPremium(User user, Policy policy, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPolicy(policy);
        payment.setAmount(amount);
        payment.setPaymentType(Payment.PaymentType.PREMIUM);
        payment.setTransactionId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setDescription("Premium Payment for Policy: " + policy.getPolicyNumber());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        return paymentRepository.save(payment);
    }
    
    public List<Payment> getUserPayments(User user) {
        return paymentRepository.findByUserOrderByPaymentDateDesc(user);
    }
}