package flab.commercemarket.domain.wishlist.vo;

import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WishList {

    private Long id;
    private long productId;
    private long userId;

    public WishListResponseDto toWishlistResponseDto() {
        return WishListResponseDto.builder()
                .id(id)
                .productId(productId)
                .userId(userId)
                .build();
    }
}
