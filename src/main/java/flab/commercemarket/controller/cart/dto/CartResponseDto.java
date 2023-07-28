package flab.commercemarket.controller.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private Long id;
    private long userId;
    private long productId;
    private int quantity;
}
