package flab.commercemarket.domain.payment.mapper;

import flab.commercemarket.domain.payment.vo.Payment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {

    void insertPayment(Payment payment);

    Optional<Payment> findById(long paymentId);

    List<Payment> findByUsername(String username, int offset, int limit);

    int countByUsername(String username);

    boolean isAlreadyExistentMerchantUid(String merchantUid);
}
