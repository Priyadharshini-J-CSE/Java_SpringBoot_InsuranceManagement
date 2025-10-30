package com.insurance.service;

import com.insurance.entity.*;
import com.insurance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    public Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user).orElse(null);
    }
    
    public void addToWallet(User user, BigDecimal amount, String description) {
        Wallet wallet = getUserWallet(user);
        if (wallet == null) {
            // Create wallet if it doesn't exist
            wallet = new Wallet(user);
            wallet.setBalance(BigDecimal.ZERO);
        }
        
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepository.save(wallet);
    }
    
    public Wallet createWallet(User user) {
        // Check if wallet already exists
        Wallet existingWallet = getUserWallet(user);
        if (existingWallet != null) {
            return existingWallet;
        }
        
        // Create new wallet
        Wallet wallet = new Wallet(user);
        wallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }
    
    public Wallet updateWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}