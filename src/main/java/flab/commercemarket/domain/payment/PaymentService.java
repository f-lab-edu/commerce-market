package flab.commercemarket.domain.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.PaymentMismatchException;
import flab.commercemarket.controller.payment.dto.PaymentCancelRequestDto;
import flab.commercemarket.controller.payment.dto.PaymentResponseDataDto;
import flab.commercemarket.controller.payment.dto.PaymentResponseDataResponseDto;
import flab.commercemarket.domain.order.OrderService;
import flab.commercemarket.domain.order.vo.Order;
import flab.commercemarket.domain.payment.repository.PaymentRepository;
import flab.commercemarket.domain.payment.vo.ApiKeyInfo;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.domain.payment.vo.PaymentPrepareRequestInfo;
import flab.commercemarket.domain.payment.vo.PaymentStatus;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${iamport.host}")
    private String host;
    @Value("${iamport.token}")
    private String getTokenEndPoint;
    @Value("${iamport.prepare}")
    private String prepareEndpoint;
    @Value("${iamport.cancel}")
    private String cancelEndpoint;
    @Value("${iamport.imp_key}")
    private String apiKey;
    @Value("${iamport.imp_secret}")
    private String apiSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;

    // 주문 발생시 비동기 처리
    @Transactional
    public void preparePayment(long orderId) { // 사전검증

        Order foundOrder = orderService.getOrder(orderId);
        log.info("merchantUid: {}", foundOrder.getMerchantUid());
        log.info("amount: {}", foundOrder.getOrderPrice());

        String merchantUid = foundOrder.getMerchantUid();
        BigDecimal amount = foundOrder.getOrderPrice();

        HttpStatus httpStatus = callPrepareApi(foundOrder.getMerchantUid(), foundOrder.getOrderPrice());

        if (httpStatus.is2xxSuccessful()) {
            savePreValidationDataToDB(merchantUid, amount);
        }
    }

    // todo fetchJoin 사용하도록 변경
    @Transactional
    public Payment processPayment(String merchantUid, String impUid, String status) {
        PaymentResponseDataDto paymentResponse = getPaymentFromPgServer(impUid);
        log.info("paymentResponse: {}", paymentResponse);

        Payment payment = paymentBuilder(merchantUid, status, paymentResponse);

        Order order = orderService.getOrder(payment.getOrderId()); // 1차 캐시 활용

        int paidPrice = paymentResponse.getAmount().intValue();
        int orderPrice = order.getOrderPrice().intValue();
        log.info("주문 예정 금액: {}", orderPrice);
        log.info("결제된 금액: {}", paidPrice);

        if (paidPrice != orderPrice) {
            cancelPaymentFromPgServer(impUid);
            throw new PaymentMismatchException("주문금액과 결제금액이 일치하지 않습니다.");
        }

        return paymentRepository.save(payment);
    }

    public Payment getPayment(long paymentId) {
        log.info("Start getPayment.");

        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        return optionalPayment.orElseThrow(() -> {
            log.info("paymentId = {}", paymentId);
            return new DataNotFoundException("조회한 결제 정보가 없음");
        });
    }

    public List<Payment> getPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return paymentRepository.findPayments(pageable);
    }

    public long countPayments() {
        log.info("count payments.");
        return paymentRepository.countPayments();
    }

    private void savePreValidationDataToDB(String merchantUid, BigDecimal amount) {
        log.info("savePreValidationDataToDB()");
        Payment preparePayment = Payment.builder()
                .status(PaymentStatus.READY)
                .merchantUid(merchantUid)
                .amount(amount)
                .build();

        paymentRepository.save(preparePayment);
    }

    private Payment paymentBuilder(String merchantUid, String status, PaymentResponseDataDto paymentResponse) {
        Order foundOrder = orderService.getOrderByMerchantUid(merchantUid);
        User foundUser = userService.getUserById(foundOrder.getUserId());

        PaymentStatus paymentStatus = convertStatusToPaymentStatus(status);

        LocalDateTime[] times = getLocalDateTimes(paymentResponse);
        LocalDateTime paidAt = times[0];
        LocalDateTime cancelledAt = times[1];
        LocalDateTime failedAt = times[2];

        return Payment.builder()
                .receiptUrl(paymentResponse.getReceiptUrl())
                .amount(paymentResponse.getAmount())
                .impUid(paymentResponse.getImpUid())
                .merchantUid(paymentResponse.getMerchantUid())
                .pgProvider(paymentResponse.getPgProvider())
                .payMethod(paymentResponse.getPayMethod())
                .status(paymentStatus)
                .order(foundOrder)
                .user(foundUser)
                .paidAt(paidAt)
                .cancelledAt(cancelledAt)
                .failedAt(failedAt)
                .build();
    }

    private String getToken() {
        log.info("Start getToken. url: {}", getTokenEndPoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ApiKeyInfo apiKeyInfo = ApiKeyInfo.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();

            String requestBody = objectMapper.writeValueAsString(apiKeyInfo);
            log.info("Complete serialization.");

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("Call Get Token API. url: {}", getTokenEndPoint);
            ResponseEntity<String> responseEntity = restTemplate.exchange(getTokenEndPoint, HttpMethod.POST, requestEntity, String.class);

            String responseBody = responseEntity.getBody();
            log.info("Complete Get Token. ResponseBody: {}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String accessToken = jsonNode.path("response").path("access_token").asText();
            return accessToken;
        } catch (JsonProcessingException e) {
            throw new RestClientException("Fail to json parsing", e);
        }
    }

    private HttpStatus callPrepareApi(String merchantUid, BigDecimal amount) {
        log.info("Start persistRequestData. apiUrl: {}", prepareEndpoint);

        String accessToken = getToken();
        log.info("Access Token: {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        PaymentPrepareRequestInfo prepareRequestInfo = PaymentPrepareRequestInfo.builder()
                .merchantUid(merchantUid)
                .amount(amount)
                .build();

        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(prepareRequestInfo);
        } catch (JsonProcessingException e) {
            throw new RestClientException("Fail to json parsing", e);
        }
        log.info("requestBody: {}", requestBody);

        ResponseEntity<String> response = restTemplate.exchange(
                prepareEndpoint,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class);
        log.info("response: {}", response);

        return response.getStatusCode();
    }

    // PG사에서 결제내역 단건조회
    private PaymentResponseDataDto getPaymentFromPgServer(String impUid) {
        String apiUrl = urlBuilder(host, "payments/" + impUid);

        String accessToken = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        log.info("Start API call. apiUrl: {}", apiUrl);
        ResponseEntity<PaymentResponseDataResponseDto> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentResponseDataResponseDto.class);
        log.info("Success API call. response: {}", responseEntity);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.warn("API 호출에 실패했습니다. 상태 코드: {}", responseEntity.getStatusCodeValue());
            throw new RuntimeException();
        }

        PaymentResponseDataResponseDto responseBody = responseEntity.getBody();
        if (responseBody == null) {
            log.warn("API Response 값이 Null 입니다.");
            throw new RuntimeException();
        }

        return responseBody.getResponse();
    }

    public void cancelPaymentFromPgServer(String impUid) {
        log.info("주문 취소 요청. endpoint: {}", cancelEndpoint);

        String accessToken = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        PaymentCancelRequestDto requestData = PaymentCancelRequestDto.builder().impUid(impUid).build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestData);
        } catch (JsonProcessingException e) {
            throw new RestClientException("Fail to json parsing", e);
        }

        ResponseEntity<PaymentResponseDataDto> responseEntity = restTemplate.exchange(
                cancelEndpoint,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                PaymentResponseDataDto.class
        );

        log.info("responseEntity: {}", responseEntity);
    }

    private PaymentStatus convertStatusToPaymentStatus(String status) {
        switch (status) {
            case "failed":
                return PaymentStatus.FAILED;
            case "canceled":
                return PaymentStatus.CANCELED;
            case "ready":
                return PaymentStatus.READY;
            case "paid":
                return PaymentStatus.PAID;
            default:
                log.warn("payment status 변환 실패. status: {}", status);
                throw new IllegalArgumentException();
        }
    }

    private LocalDateTime[] getLocalDateTimes(PaymentResponseDataDto paymentResponse) {
        long[] timestamps = {
                paymentResponse.getPaidAt(),
                paymentResponse.getCancelledAt(),
                paymentResponse.getFailedAt()
        };

        LocalDateTime[] times = new LocalDateTime[timestamps.length];
        for (int i = 0; i < timestamps.length; i++) {
            if (timestamps[i] != 0) {
                Instant instant = Instant.ofEpochSecond(timestamps[i]);
                times[i] = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
        }
        return times;
    }

    private String urlBuilder(String host, String path) {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host)
                .path(path)
                .build()
                .toString();
    }
}
