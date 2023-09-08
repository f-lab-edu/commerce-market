package flab.commercemarket.domain.cart.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.cart.vo.QCart;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CartRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Cart> findCartByUserId(long userId, Pageable pageable) {
        return queryFactory
                .selectFrom(QCart.cart)
                .where(QCart.cart.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countCartByUserId(long userId) {
        Long totalCount = queryFactory
                .select(QCart.cart.count())
                .from(QCart.cart)
                .where(QCart.cart.user.id.eq(userId))
                .fetchOne();

        return totalCount != null ? totalCount : 0L;
    }

    @Override
    public List<Cart> findAllByUserId(long userId) {
        return queryFactory
                .selectFrom(QCart.cart)
                .where(QCart.cart.user.id.eq(userId))
                .fetch();
    }

    @Override
    public boolean isAlreadyExistentProductInUserCart(long userId, long productId) {
        BooleanExpression whereClause = QCart.cart.user.id.eq(userId).and(QCart.cart.product.id.eq(productId));

        Integer fetchOne = queryFactory
                .selectOne()
                .from(QCart.cart)
                .where(whereClause)
                .fetchFirst();

        return fetchOne != null;
    }

}
