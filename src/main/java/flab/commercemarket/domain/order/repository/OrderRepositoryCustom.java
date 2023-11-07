package flab.commercemarket.domain.order.repository;

import flab.commercemarket.domain.order.vo.Order;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {
    List<Order> findBetweenDateTime(long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    long countOrderBetweenDate(long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
