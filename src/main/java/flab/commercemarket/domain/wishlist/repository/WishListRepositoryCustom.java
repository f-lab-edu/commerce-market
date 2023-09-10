package flab.commercemarket.domain.wishlist.repository;

import flab.commercemarket.domain.wishlist.vo.WishList;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WishListRepositoryCustom {
    List<WishList> getWishListItemByUserId(long userId);
    List<WishList> findAllByUserId(long userId, Pageable pageable);
    long countByUserId(long userId);
}
