package flab.commercemarket.product.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String productName;
    private int price;
    private String imageUrl;
    private String description;
    private int stockAmount;
}
