package flab.commercemarket.domain.cart.mapper;

import flab.commercemarket.domain.cart.vo.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {

    void createCart(Cart cart);

    void updateCart(Cart cart);

    Optional<Cart> findById(long cartId);

    List<Cart> findAll(long userId);

    void deleteCart(long cartId);

    boolean isAlreadyExistentProductInUserCart(long userId, long productId);

    boolean isExistentProduct(long productId);
}
