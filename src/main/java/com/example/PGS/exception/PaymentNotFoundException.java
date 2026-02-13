package com.example.PGS.exception;

public class PaymentNotFoundException extends BusinessException {

    public PaymentNotFoundException() {
        super("Payment not found");
    }
}