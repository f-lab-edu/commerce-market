package flab.commercemarket.controller.cart.dto;

import flab.commercemarket.domain.cart.vo.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartDto {
    private long userId;
    private long productId;
    private int quantity;

    public Cart toCart() {
        return Cart.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
