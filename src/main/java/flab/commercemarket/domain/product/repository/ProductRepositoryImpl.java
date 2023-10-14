package flab.commercemarket.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.product.vo.QProduct;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Product> findByKeyword(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(QProduct.product)
                .where(QProduct.product.name.like(keyword + "%"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchProductByKeyword(String keyword) {
        return queryFactory
                .selectFrom(QProduct.product)
                .where(QProduct.product.name.like(keyword + "%"))
                .stream()
                .count();
    }
}
