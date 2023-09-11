package flab.commercemarket.controller.order.dto;

import flab.commercemarket.domain.order.vo.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private long id;
    private long userId;
    private List<OrderProduct> orderProduct;
    private String requestMessage;
    private LocalDateTime orderedAt;
    private BigDecimal orderPrice;
}
