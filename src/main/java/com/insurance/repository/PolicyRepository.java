package com.insurance.repository;

import com.insurance.entity.Policy;
import com.insurance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByUser(User user);
    List<Policy> findByStatus(Policy.PolicyStatus status);
    List<Policy> findByUserAndStatus(User user, Policy.PolicyStatus status);
}