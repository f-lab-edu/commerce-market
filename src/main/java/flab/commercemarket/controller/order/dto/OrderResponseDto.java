package flab.commercemarket.controller.order.dto;

import flab.commercemarket.domain.order.vo.OrderProduct;
import flab.commercemarket.domain.user.vo.User;
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
    private User user;
    private List<OrderProduct> orderProduct;
    private LocalDateTime orderedAt;
    private BigDecimal orderPrice;
    private String merchantUid;
}
