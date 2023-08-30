package flab.commercemarket.controller.product.dto;

import flab.commercemarket.domain.product.vo.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @NotNull(message = "Name cannot be null")
    private String name;

    @Min(value = 0, message = "Price cannot be negative")
    private int price;

    @NotNull(message = "Image URL cannot be null")
    private String imageUrl;

    @NotNull(message = "Description cannot be null")
    private String description;

    @Min(value = 0, message = "Stock amount cannot be negative")
    private int stockAmount;

    @Positive(message = "Seller ID must be a positive value")
    private long sellerId;

    public Product toProduct() {
        return Product.builder()
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .stockAmount(stockAmount)
                .sellerId(sellerId)
                .build();
    }
}
