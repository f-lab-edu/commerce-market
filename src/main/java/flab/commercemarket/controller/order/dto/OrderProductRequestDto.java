package flab.commercemarket.controller.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductRequestDto {
    private long productId;
    private int quantity;
}
