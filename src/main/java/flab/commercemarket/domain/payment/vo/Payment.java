package flab.commercemarket.domain.payment.vo;

import flab.commercemarket.controller.payment.dto.PaymentResponseDto;
import flab.commercemarket.domain.order.vo.Order;
import flab.commercemarket.domain.user.vo.User;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String impUid;
    private String merchantUid;
    private String payMethod;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private LocalDateTime cancelledAt;
    private String receiptUrl;
    private String pgProvider;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public long getOrderId() {
        return order.getId();
    }

    public PaymentResponseDto toPaymentResponseDto() {
        return PaymentResponseDto.builder()
                .id(id)
                .impUid(impUid)
                .merchantUid(merchantUid)
                .payMethod(payMethod)
                .amount(amount)
                .status(status)
                .paidAt(paidAt)
                .failedAt(failedAt)
                .cancelledAt(cancelledAt)
                .receiptUrl(receiptUrl)
                .pgProvider(pgProvider)
                .orderId(order.getId())
                .userId(user.getId())
                .build();
    }
}
