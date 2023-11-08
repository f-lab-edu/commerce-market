package flab.commercemarket.domain.cart.repository;

import flab.commercemarket.domain.cart.vo.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
}
