package flab.commercemarket.domain.payment.repository;

import flab.commercemarket.domain.payment.vo.Payment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentRepositoryCustom {

    List<Payment> findPayments(Pageable pageable);
    long countPayments();
}
