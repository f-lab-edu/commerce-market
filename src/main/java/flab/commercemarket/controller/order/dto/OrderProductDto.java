package flab.commercemarket.controller.order.dto;

import flab.commercemarket.controller.product.dto.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrderProductDto {
    private long id;
    private ProductResponseDto productResponseDto;
    private int quantity;
}
