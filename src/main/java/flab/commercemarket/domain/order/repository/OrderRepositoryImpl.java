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
    public List<Order> findBetweenDateTime(long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        return queryFactory
                .selectFrom(order)
                .where(order.orderedAt.between(startDateTime, endDateTime).and(order.user.id.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countOrderBetweenDate(long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory
                .select(order.count())
                .from(order)
                .where(order.user.id.eq(userId)
                        .and(order.orderedAt.between(startDateTime, endDateTime)))
                .fetchOne();
    }
}
