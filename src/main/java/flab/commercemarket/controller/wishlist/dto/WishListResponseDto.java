package flab.commercemarket.controller.wishlist.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishListResponseDto {

    private long id;
    private long productId;
    private long userId;
}
