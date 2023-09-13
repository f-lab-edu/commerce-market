package flab.commercemarket.controller.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartDto {
    private long userId;
    private long productId;
    private int quantity;
}
