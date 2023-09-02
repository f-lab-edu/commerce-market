package flab.commercemarket.domain.payment.mapper;

import flab.commercemarket.domain.payment.vo.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {

    void insertPrepareRequestData(Payment payment);

    Optional<Payment> findById(long paymentId);

    Optional<Payment> findByMerchantUid(String merchantUid);

    List<Payment> findByUsername(String username, int offset, int limit);

    int countByBuyerName(String username);

    boolean isAlreadyExistentMerchantUid(String merchantUid);

    void updateCompletePayment(@Param("id") long id, @Param("payment") Payment payment);

    void updateCancelPayment(@Param("id") long id, @Param("payment") Payment payment);
}
