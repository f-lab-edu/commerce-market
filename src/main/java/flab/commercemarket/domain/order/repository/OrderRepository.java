package flab.commercemarket.domain.order.repository;

import flab.commercemarket.domain.order.vo.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findByMerchantUid(String merchantUid);
}
