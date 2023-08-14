package flab.commercemarket.domain.payment.vo;

import flab.commercemarket.controller.payment.dto.PaymentResponseDto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private String applyNum;
    private String bankName;
    private String buyerAddr;
    private String buyerEmail;
    private String buyerName;
    private String buyerTel;
    private String cardName;
    private String cardNumber;
    private int cardQuota;
    private String customData;
    private String merchantUid;
    private String name;
    private int paidAmount;
    private String payMethod;
    private String pgProvider;

    @Setter private String pgTid;
    @Setter private String pgType;
    @Setter private String currency;
    @Setter private long paidAt;
    @Setter private String receiptUrl;
    @Setter private String status;
    @Setter private String impUid;
    @Setter private boolean success;

    @Builder
    public Payment(String applyNum, String bankName, String buyerAddr, String buyerEmail, String buyerName, String buyerTel, String cardName, String cardNumber, int cardQuota, String currency, String customData, String impUid, String merchantUid, String name, int paidAmount, long paidAt, String payMethod, String pgProvider, String pgTid, String pgType, String receiptUrl, String status, boolean success) {
        this.applyNum = applyNum;
        this.bankName = bankName;
        this.buyerAddr = buyerAddr;
        this.buyerEmail = buyerEmail;
        this.buyerName = buyerName;
        this.buyerTel = buyerTel;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardQuota = cardQuota;
        this.currency = currency;
        this.customData = customData;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.name = name;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
        this.payMethod = payMethod;
        this.pgProvider = pgProvider;
        this.pgTid = pgTid;
        this.pgType = pgType;
        this.receiptUrl = receiptUrl;
        this.status = status;
        this.success = success;
    }

    public PaymentResponseDto toPaymentResponseDto() {
        return PaymentResponseDto.builder()
                .success(success)
                .impUid(impUid)
                .payMethod(payMethod)
                .merchantUid(merchantUid)
                .name(name)
                .paidAmount(paidAmount)
                .currency(currency)
                .pgProvider(pgProvider)
                .pgType(pgType)
                .pgTid(pgTid)
                .applyNum(applyNum)
                .buyerName(buyerName)
                .buyerEmail(buyerEmail)
                .buyerTel(buyerTel)
                .buyerAddr(buyerAddr)
                .customData(customData)
                .status(status)
                .paidAt(paidAt)
                .receiptUrl(receiptUrl)
                .cardName(cardName)
                .bankName(bankName)
                .cardQuota(cardQuota)
                .cardNumber(cardNumber)
                .build();
    }
}
