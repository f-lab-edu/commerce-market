package flab.commercemarket.controller.product.dto;

import flab.commercemarket.domain.product.vo.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
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
