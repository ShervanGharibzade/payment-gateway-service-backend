package com.example.PGS.entity.enums;

import lombok.Getter;
import lombok.Setter;

public enum PaymentStatus {
    INITIATED(false),
    PENDING(false),
    SUCCESS(true),
    FAILED(true),
    CANCELED(true);


    private final boolean isFinal;

    PaymentStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }


}
