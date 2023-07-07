package flab.commercemarket.cart.dto;

import flab.commercemarket.cart.domain.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
