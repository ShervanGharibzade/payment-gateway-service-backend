package com.example.PGS.security;

import com.example.PGS.dto.CallbackRequest;
import com.example.PGS.dto.CreatePaymentRequest;
import com.example.PGS.entity.Merchant;
import com.example.PGS.entity.Payment;
import com.example.PGS.entity.enums.PaymentStatus;
import com.example.PGS.exception.DuplicatePaymentException;
import com.example.PGS.exception.MerchantNotFoundException;
import com.example.PGS.exception.PaymentNotFoundException;
import com.example.PGS.repository.MerchantRepository;
import com.example.PGS.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;

    @Transactional
    public Payment createPayment(CreatePaymentRequest req) {

        if (paymentRepository.existsByReferenceNumber(req.getReferenceNumber())) {
            throw new DuplicatePaymentException();
        }

        Merchant merchant = merchantRepository.findById(req.getMerchantId())
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found with ID: " + req.getMerchantId()
                        )
                );

        Payment payment = Payment.builder()
                .merchant(merchant)
                .amount(req.getAmount())
                .referenceNumber(req.getReferenceNumber())
                .gateway(req.getGateway())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public void handleCallback(CallbackRequest req) {

        Payment payment = paymentRepository
                .findByReferenceNumberAndGateway(
                        req.getReferenceNumber(),
                        req.getGateway()
                )
                .orElseThrow(PaymentNotFoundException::new);


        if (payment.getStatus().isFinal()) {
            return;
        }

        if (req.isSuccessful()) {
            payment.markAsPaid(req.getTrackingNumber());
        } else {
            payment.markAsFailed(req.getFailureReason());
        }
    }
}
