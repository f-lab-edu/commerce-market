package flab.commercemarket.cart.domain;

import flab.commercemarket.cart.dto.CartResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Cart {
    private Long id;
    private long userId;
    private long productId;
    private int quantity;

    @Builder
    public Cart(long userId, long productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartResponseDto toCartResponseDto() {
        return CartResponseDto.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
