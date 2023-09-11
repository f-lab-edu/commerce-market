package flab.commercemarket.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.order.vo.Order;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static flab.commercemarket.domain.order.vo.QOrder.order;

public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Order> findBetweenDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        return queryFactory
                .selectFrom(order)
                .where(order.orderedAt.between(startDateTime, endDateTime))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countOrderBetweenDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory
                .selectFrom(order)
                .where(order.orderedAt.between(startDateTime, endDateTime))
                .stream()
                .count();
    }
}
