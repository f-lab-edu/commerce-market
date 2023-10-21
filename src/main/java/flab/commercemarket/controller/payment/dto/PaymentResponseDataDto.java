package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class PaymentResponseDataDto {
    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

    @JsonProperty("pay_method")
    private String payMethod;

    @JsonProperty("amount")
    private BigDecimal amount;

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

    @JsonProperty("buyer_email")
    private String buyerEmail;

}
