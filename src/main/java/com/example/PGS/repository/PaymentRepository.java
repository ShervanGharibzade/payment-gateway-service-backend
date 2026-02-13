package com.example.PGS.repository;

import com.example.PGS.entity.Payment;
import com.example.PGS.entity.enums.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    Optional<Payment> findByReferenceNumberAndGateway(
            String referenceNumber,
            PaymentGateway gateway
    );

}
