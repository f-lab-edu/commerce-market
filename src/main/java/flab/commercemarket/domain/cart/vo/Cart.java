package flab.commercemarket.domain.cart.vo;

import flab.commercemarket.controller.cart.dto.CartResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
