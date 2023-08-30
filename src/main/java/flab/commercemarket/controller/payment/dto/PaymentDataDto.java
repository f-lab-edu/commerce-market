package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDataDto {
    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

    @JsonProperty("pay_method")
    private String payMethod;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("paid_at")
    private long paidAt;

    @JsonProperty("failed_at")
    private long failedAt;

    @JsonProperty("cancelled_at")
    private long cancelledAt;

    @JsonProperty("receipt_url")
    private String receiptUrl;

    @JsonProperty("pg_provider")
    private String pgProvider;

    @JsonProperty("buyer_name")
    private String buyerName;

    public Payment toCompletePayment() {
        return Payment.builder()
                .impUid(impUid)
                .payMethod(payMethod)
                .status(status)
                .paidAt(LocalDateTime.now())
                .receiptUrl(receiptUrl)
                .pgProvider(pgProvider)
                .buyerName(buyerName)
                .success(true)
                .build();
    }

    public Payment toCanceledPayment() {
        return Payment.builder()
                .impUid(impUid)
                .payMethod(payMethod)
                .status(status)
                .cancelledAt(LocalDateTime.now())
                .receiptUrl(receiptUrl)
                .pgProvider(pgProvider)
                .buyerName(buyerName)
                .success(true)
                .build();
    }
}
