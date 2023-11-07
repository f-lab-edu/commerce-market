package flab.commercemarket.controller.order.dto;

import flab.commercemarket.controller.user.dto.UserResponseDto;
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
public class OrderGetResponseDto {
    private long id;
    private UserResponseDto user;
    private List<OrderProductDto>orderProductDto;
    private LocalDateTime orderedAt;
    private BigDecimal orderPrice;
    private String merchantUid;
}
