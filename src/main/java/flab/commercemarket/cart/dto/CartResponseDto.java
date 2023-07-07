package flab.commercemarket.cart.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private Long id;
    private long userId;
    private long productId;
    private int quantity;
}
