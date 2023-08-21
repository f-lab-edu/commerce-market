package flab.commercemarket.domain.payment.vo;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PREPARE("prepare"),
    SUCCESS("success"),
    FAILED("failed"),
    CANCELLED("cancelled");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
