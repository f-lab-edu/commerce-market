package flab.commercemarket.controller.product.dto;

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
}
