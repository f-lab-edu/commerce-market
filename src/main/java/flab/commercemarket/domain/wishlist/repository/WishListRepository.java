package flab.commercemarket.domain.wishlist.repository;

import flab.commercemarket.domain.wishlist.vo.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long>, WishListRepositoryCustom {
}
