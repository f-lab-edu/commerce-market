package flab.commercemarket.cart.mapper;

import flab.commercemarket.cart.domain.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {

    void createCart(Cart cart);

    void updateCart(Cart cart);

    Optional<Cart> findById(long cartId);

    boolean checkCartExistence(long userId, long productId);

    List<Cart> findAll(long userId);

    void deleteCart(long cartId);

}
