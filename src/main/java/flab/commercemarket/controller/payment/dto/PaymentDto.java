package flab.commercemarket.controller.payment.dto;

import flab.commercemarket.domain.payment.vo.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentDto {
    private String pg;
    private String payMethod;
    private String merchantUid;
    private String name;
    private int amount;
    private String buyerEmail;
    private String buyerName;
    private String buyerTel;
    private String buyerAddr;

    public Payment toPayment() {
        return Payment.builder()
                .pgProvider(pg)
                .payMethod(payMethod)
                .merchantUid(merchantUid)
                .name(name)
                .paidAmount(amount)
                .buyerEmail(buyerEmail)
                .buyerName(buyerName)
                .buyerTel(buyerTel)
                .buyerAddr(buyerAddr)
                .build();
    }
}
