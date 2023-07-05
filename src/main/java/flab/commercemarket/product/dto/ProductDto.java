package flab.commercemarket.product.dto;

import flab.commercemarket.product.domain.Product;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;
    private int price;
    private String imageUrl;
    private String description;
    private int stockAmount;

    public Product toProduct() {
        return Product.builder()
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .stockAmount(stockAmount)
                .build();
    }
}
