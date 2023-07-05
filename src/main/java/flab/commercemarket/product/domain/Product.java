package flab.commercemarket.product.domain;

import flab.commercemarket.product.dto.ProductResponseDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private String description;
    private int stockAmount;
    private int salesAmount;
    private int likeCount;
    private int dislikeCount;
    private long sellerId;

    public ProductResponseDto toProductResponseDto() {
        return ProductResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .stockAmount(stockAmount)
                .build();
    }
}
