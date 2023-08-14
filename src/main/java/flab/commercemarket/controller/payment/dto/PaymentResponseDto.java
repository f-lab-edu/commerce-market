package flab.commercemarket.controller.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private boolean success;
    private String impUid;
    private String payMethod;
    private String merchantUid;
    private String name;
    private int paidAmount;
    private String currency;
    private String pgProvider;
    private String pgType;
    private String pgTid;
    private String applyNum;
    private String buyerName;
    private String buyerEmail;
    private String buyerTel;
    private String buyerAddr;
    private String customData;
    private String status;
    private long paidAt;
    private String receiptUrl;
    private String cardName;
    private String bankName;
    private int cardQuota;
    private String cardNumber;
}
