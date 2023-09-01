package flab.commercemarket.domain.wishlist.repository;

import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.wishlist.vo.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Page<WishList> findAllByUser(Pageable pageable, User user);

    Optional<WishList> getWishListById(long wishListId);

    // select * from wish_list where user_id = #{userId} and product_id = #{productId};
    @Query("SELECT w FROM WishList w WHERE w.user.id = :userId AND w.product.id = :productId")
    List<WishList> isExistProductInUserWishList(long userId, long productId);
}
