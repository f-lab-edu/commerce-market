package flab.commercemarket.domain.wishlist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.wishlist.vo.QWishList;
import flab.commercemarket.domain.wishlist.vo.WishList;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

public class WishListRepositoryImpl implements WishListRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WishListRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<WishList> getWishListItemByUserId(long userId) {
        return queryFactory
                .selectFrom(QWishList.wishList)
                .where(QWishList.wishList.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<WishList> findAllByUserId(long userId, Pageable pageable) {
        return queryFactory
                .selectFrom(QWishList.wishList)
                .where(QWishList.wishList.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countByUserId(long userId) {
        return queryFactory
                .selectFrom(QWishList.wishList)
                .where(QWishList.wishList.user.id.eq(userId))
                .stream()
                .count();
    }
}
