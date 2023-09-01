package flab.commercemarket.domain.wishlist.vo;

import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.vo.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public WishListResponseDto toWishlistResponseDto() {
        return WishListResponseDto.builder()
                .id(id)
                .userId(user.getId())
                .productId(product.getId())
                .build();
    }

    public long getProductId() {
        return product.getId();
    }

    public long getUserId() {
        return user.getId();
    }
}
