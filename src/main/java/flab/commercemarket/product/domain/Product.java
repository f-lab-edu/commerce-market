package flab.commercemarket.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String productName;
    private int price;
    private String imageUrl;
    private String description;
    private int stockAmount;
    private int salesAmount;
    private int likeCount;
    private int dislikeCount;
    private long sellerId;
}
