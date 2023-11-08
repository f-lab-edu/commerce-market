package flab.commercemarket.domain.product.vo;

import flab.commercemarket.controller.product.dto.ProductResponseDto;
import flab.commercemarket.domain.user.vo.User;
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
@Table(indexes = @Index(name = "idx_product_name", columnList = "name"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private String description;
    private int likeCount;

    @ManyToOne
    private User seller;

    @Version
    private Long version;

    public ProductResponseDto toProductResponseDto() {
        return ProductResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .description(description)
                .likeCount(likeCount)
                .sellerId(seller.getId())
                .build();
    }
}
