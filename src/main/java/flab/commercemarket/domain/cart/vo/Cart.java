package flab.commercemarket.domain.cart.vo;

import flab.commercemarket.controller.cart.dto.CartResponseDto;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.vo.User;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
})
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Builder
    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public CartResponseDto toCartResponseDto() {
        return CartResponseDto.builder()
                .id(id)
                .userId(user.getId())
                .productId(product.getId())
                .quantity(quantity)
                .build();
    }

    public long getUserId() {
        return user.getId();
    }

    public long getProductId() {
        return product.getId();
    }
}
