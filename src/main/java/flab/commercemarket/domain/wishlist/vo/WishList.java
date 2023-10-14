package flab.commercemarket.domain.wishlist.vo;

import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Product product;

    @OneToOne
    private User user;

    public WishListResponseDto toWishlistResponseDto() {
        return WishListResponseDto.builder()
                .id(id)
                .productId(product.getId())
                .userId(user.getId())
                .build();
    }

    public long getUserId() {
        return user.getId();
    }
}
