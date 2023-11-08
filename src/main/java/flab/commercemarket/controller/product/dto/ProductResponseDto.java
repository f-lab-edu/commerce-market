package flab.commercemarket.controller.product.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private String description;
    private int likeCount;
    private Long sellerId;
}
