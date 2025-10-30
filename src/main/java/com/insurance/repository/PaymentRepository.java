package com.insurance.repository;

import com.insurance.entity.Payment;
import com.insurance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserOrderByPaymentDateDesc(User user);
    List<Payment> findByUserAndPaymentType(User user, Payment.PaymentType paymentType);
}