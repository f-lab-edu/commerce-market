package flab.commercemarket.domain.cart.repository;


import flab.commercemarket.domain.cart.vo.Cart;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartRepositoryCustom {
    List<Cart> findCartByUserId(long userId, Pageable pageable);
    long countCartByEmail(String email);
    List<Cart> findAllByUserId(long userId);
    boolean isAlreadyExistentProductInUserCart(long userId, long productId);
}
