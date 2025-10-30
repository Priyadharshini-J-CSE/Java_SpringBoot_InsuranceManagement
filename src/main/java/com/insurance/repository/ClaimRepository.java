package com.insurance.repository;

import com.insurance.entity.Claim;
import com.insurance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUser(User user);
    List<Claim> findByStatus(Claim.ClaimStatus status);
    List<Claim> findByUserOrderBySubmittedDateDesc(User user);
}