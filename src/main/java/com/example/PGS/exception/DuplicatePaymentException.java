package com.example.PGS.exception;

public class DuplicatePaymentException extends BusinessException {

    public DuplicatePaymentException() {
        super("Duplicate payment reference number");
    }
}