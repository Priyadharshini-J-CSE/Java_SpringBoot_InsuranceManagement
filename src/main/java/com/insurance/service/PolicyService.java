package com.insurance.service;

import com.insurance.entity.Policy;
import com.insurance.entity.User;
import com.insurance.entity.Notification;
import com.insurance.repository.PolicyRepository;
import com.insurance.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyService {
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Policy applyForPolicy(Policy policy) {
        policy.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return policyRepository.save(policy);
    }
    
    public List<Policy> getUserPolicies(User user) {
        return policyRepository.findByUser(user);
    }
    
    public List<Policy> getPendingPolicies() {
        return policyRepository.findByStatus(Policy.PolicyStatus.PENDING);
    }
    
    public void approvePolicy(Long policyId) {
        Policy policy = policyRepository.findById(policyId).orElse(null);
        if (policy != null) {
            policy.setStatus(Policy.PolicyStatus.APPROVED);
            policy.setApprovedDate(LocalDateTime.now());
            policyRepository.save(policy);
            
            // Send notification
            Notification notification = new Notification(
                policy.getUser(),
                "Policy Approved",
                "Your policy " + policy.getPolicyNumber() + " has been approved."
            );
            notificationRepository.save(notification);
        }
    }
    
    public void rejectPolicy(Long policyId) {
        Policy policy = policyRepository.findById(policyId).orElse(null);
        if (policy != null) {
            policy.setStatus(Policy.PolicyStatus.REJECTED);
            policyRepository.save(policy);
            
            // Send notification
            Notification notification = new Notification(
                policy.getUser(),
                "Policy Rejected",
                "Your policy " + policy.getPolicyNumber() + " has been rejected."
            );
            notificationRepository.save(notification);
        }
    }
    
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
    
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }
}