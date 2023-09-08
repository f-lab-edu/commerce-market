package flab.commercemarket.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.product.vo.Product;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static flab.commercemarket.domain.product.vo.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Product> findByKeyword(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(product)
                .from(product)
                .where(product.name.contains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchProductByKeyword(String keyword) {
        Long totalCount = queryFactory
                .select(product.count())
                .from(product)
                .fetchOne();
        return totalCount != null ? totalCount : 0L;
    }
}
