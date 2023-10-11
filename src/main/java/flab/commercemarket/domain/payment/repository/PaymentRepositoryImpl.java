package flab.commercemarket.domain.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.domain.payment.vo.QPayment;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static flab.commercemarket.domain.payment.vo.QPayment.payment;

public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PaymentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Payment> findPayments(Pageable pageable) {
        return queryFactory
                .selectFrom(payment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countPayments() {
        return queryFactory.selectFrom(payment)
                .stream()
                .count();
    }
}
