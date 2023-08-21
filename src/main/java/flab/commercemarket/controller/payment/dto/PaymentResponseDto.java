package flab.commercemarket.controller.payment.dto;

import flab.commercemarket.domain.payment.vo.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private String impUid;
    private String merchantUid;
    private String payMethod;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private LocalDateTime cancelledAt;
    private String receiptUrl;
    private String pgProvider;
    private String buyerName;
    private boolean success;
}
