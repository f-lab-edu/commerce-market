package flab.commercemarket.domain.product.vo;

import flab.commercemarket.controller.product.dto.ProductResponseDto;
import lombok.*;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@OptimisticLocking
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private String description;
    private int stockAmount;
    private int salesAmount;
    private int likeCount;
    private long sellerId;

    @Version
    private Long version;

    public ProductResponseDto toProductResponseDto() {
        return ProductResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .stockAmount(stockAmount)
                .salesAmount(salesAmount)
                .likeCount(likeCount)
                .sellerId(sellerId)
                .build();
    }
}
