package flab.commercemarket.domain.wishlist.mapper;

import flab.commercemarket.domain.wishlist.vo.WishList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface WishListMapper {

    void insertWishList(long userId, long productId);

    List<WishList> getWishListItemByUserId(long userId);

    Optional<WishList> findById(long id);

    void deleteWishList(long wishListId);
}
