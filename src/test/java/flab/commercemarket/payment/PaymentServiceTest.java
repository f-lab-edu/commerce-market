package flab.commercemarket.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.payment.PaymentService;
import flab.commercemarket.domain.payment.mapper.PaymentMapper;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.domain.payment.vo.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    @DisplayName("merchantUid가 중복되면 예외를 발생시킨다.")
    public void processPayment_duplicate_merchantUid() throws Exception {
        // given
        String merchantUid = "testMerchantUid";

        // when
        when(paymentMapper.isAlreadyExistentMerchantUid(merchantUid)).thenReturn(true);

        // then
        assertThrows(DuplicateDataException.class, () ->
                paymentService.processPayment(merchantUid, BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("merchantUid가 null인 경우 IllegalArgumentException이 발생한다.")
    void testCheckMerchantUidNotNull() {
        // Arrange
        String merchantUid = null;
        BigDecimal amount = BigDecimal.valueOf(100);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(merchantUid, amount);
        });
    }

    @Test
    public void getPaymentTest_validPaymentId() throws Exception {
        // given
        long paymentId = 1L;
        Payment expectedPayment = makePaymentFixture();
        when(paymentMapper.findById(paymentId)).thenReturn(Optional.of(expectedPayment));

        // when
        Payment actualPayment = paymentService.getPayment(paymentId);

        // then
        assertThat(actualPayment).isEqualTo(expectedPayment);
        verify(paymentMapper).findById(paymentId);
    }

    @Test
    @DisplayName("InvalidPaymentId가 조회되지 않으면 예외를 발생시킨다.")
    public void getPaymentTest_InvalidPaymentId_ThrowsDataNotFoundException() throws Exception {
        // given
        long paymentId = 1L;

        // when
        when(paymentMapper.findById(paymentId)).thenReturn(Optional.empty());

        // then
        assertThrows(DataNotFoundException.class, () -> paymentService.getPayment(paymentId));
        verify(paymentMapper).findById(paymentId);
    }

    @Test
    @DisplayName("특정 사용자의 결제 데이터를 정확하게 반환해야 함")
    public void getWishPaymentListTest_findByUsername() throws Exception {
        // given
        int page = 2;
        int size = 3;
        String username = "taebong";

        // when
        List<Payment> paymentList = makePaymentListFixture();
        List<Payment> actual = paymentList.stream()
                .filter(payment -> username.equals(payment.getBuyerName()))
                .collect(Collectors.toList());

        when(paymentMapper.findByUsername(username, (page-1) * size, size)).thenReturn(actual);
        List<Payment> result = paymentService.getPayments(username, page, size);

        // then
        assertThat(actual).isEqualTo(result);
    }

    @Test
    @DisplayName("페이징 처리된 결제 데이터를 반환해야 함")
    public void getWishPaymentListTest_pagination() throws Exception {
        // given
        int page = 2;
        int size = 3;
        String username = "user A";
        List<Payment> paymentList = makePaymentListFixture();

        List<Payment> expectedPayments = paymentList.stream()
                .filter(payment -> username.equals(payment.getBuyerName()))
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        // when
        when(paymentMapper.findByUsername(username, (page - 1) * size, size)).thenReturn(expectedPayments);
        List<Payment> result = paymentService.getPayments(username, page, size);

        // then
        assertThat(result).isEqualTo(expectedPayments);

    }

    @Test
    @DisplayName("특정 유저의 결제 데이터 개수를 정확하게 반환해야 함")
    public void countPaymentByUsernameTest() throws Exception {
        // given
        String username = "user A";
        List<Payment> paymentList = makePaymentListFixture();
        long expectedCount = paymentList.stream()
                .filter(payment -> username.equals(payment.getBuyerName()))
                .count();

        when(paymentMapper.countByBuyerName(username)).thenReturn((int) expectedCount);

        // when
        int result = paymentService.countPaymentByUsername(username);

        // then
        assertThat(result).isEqualTo((int) expectedCount);
    }

    private Payment makePaymentFixture() {
        BigDecimal amount = BigDecimal.valueOf(10000.00);

        return new Payment(
                1L,
                "imp_uid1",
                "merchant_uid1",
                "card",
                amount,
                PaymentStatus.SUCCESS,
                LocalDateTime.now(),
                null,
                null,
                "receiptUrl2",
                "kakao",
                "name A",
                true
        );
    }

    private List<Payment> makePaymentListFixture() {
        BigDecimal amount = BigDecimal.valueOf(20000.00);

        List<Payment> paymentList = new ArrayList<>();
        Payment payment1 = makePaymentFixture();
        Payment payment2 = makePaymentFixture();
        Payment payment3 = makePaymentFixture();
        Payment payment4 = makePaymentFixture();

        Payment payment5 = new Payment(
                2L,
                "imp_uid2",
                "merchant_uid2",
                "card",
                amount,
                PaymentStatus.SUCCESS,
                LocalDateTime.now(),
                null,
                null,
                "receiptUrl2",
                "kakao",
                "name B",
                true
        );

        paymentList.add(payment1);
        paymentList.add(payment2);
        paymentList.add(payment3);
        paymentList.add(payment4);
        paymentList.add(payment5);

        return paymentList;
    }
}
