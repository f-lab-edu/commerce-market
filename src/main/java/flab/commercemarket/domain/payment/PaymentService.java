package flab.commercemarket.domain.payment;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.payment.mapper.PaymentMapper;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;

    public Payment processPayment(Payment payment) {
        // TODO 결제 요청을 받아 가맹점 DB에 저장하고 PG사에 결제 요청을 보내는 로직
        log.info("Start persistRequestData");

        isDuplicatedMerchantUid(payment.getMerchantUid());
        paymentMapper.insertPayment(payment);
        log.info("persistRequestData. merchantUid = {}", payment.getMerchantUid());
        return payment;
    }

    public Payment getPayment(long paymentId) {
        log.info("Start getPayment.");

        Optional<Payment> optionalPayment = paymentMapper.findById(paymentId);
        return optionalPayment.orElseThrow(() -> {
            log.info("paymentId = {}", paymentId);
            return new DataNotFoundException("조회한 결제 정보가 없음");
        });
    }

    public List<Payment> getPayments(String username, int page, int size) {
        log.info("Start getPayments. username = {}", username);

        int limit = size;
        int offset = (page - 1) * size;

        return paymentMapper.findByUsername(username, offset, limit);
    }

    public int countPaymentByUsername(String username) {
        log.info("Start countPaymentByUsername. username = {}", username);

        return paymentMapper.countByUsername(username);
    }

    private void isDuplicatedMerchantUid(String merchantUid) {
        log.info("Start isDuplicatedMerchantUid.");

        boolean result = paymentMapper.isAlreadyExistentMerchantUid(merchantUid);
        if (result) {
            log.warn("merchantUid 중복. merchantUid = {}", merchantUid);
            throw new DuplicateDataException("중복결제 시도");
        }
    }
}
