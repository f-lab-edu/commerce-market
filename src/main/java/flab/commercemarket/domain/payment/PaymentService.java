package flab.commercemarket.domain.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.controller.payment.dto.PaymentData;
import flab.commercemarket.controller.payment.dto.PaymentDataResponse;
import flab.commercemarket.domain.payment.mapper.PaymentMapper;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.domain.payment.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${imp_key}")
    private String apiKey;

    @Value("${imp_secret}")
    private String apiSecret;

    private final PaymentMapper paymentMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPayment(String merchantUid, BigDecimal amount) {
        String apiUrl = "https://api.iamport.kr/payments/prepare";

        log.info("Start persistRequestData");

        isDuplicatedMerchantUid(merchantUid);
        checkMerchantUidNotNull(merchantUid);

        callPrepareApi(merchantUid, amount, apiUrl);
        savePreValidationDataToDB(merchantUid, amount);

        log.info("Payment pre-validation successful. merchantUid: {}", merchantUid);
    }

    @Transactional
    public void completePaymentVerification(String impUid, String merchantUid) {
        log.info("Start completePaymentVerification. impUid: {}, merchantUid: {}", impUid, merchantUid);

        PaymentData paymentData = fetchPaymentFromIamportServer(impUid); // 조회한 결제정보

        // DB에서 결제되어야 하는 금액 조회
        Payment foundPayment = getPaymentByMerchantUid(merchantUid);
        BigDecimal foundAmount = foundPayment.getAmount();
        log.info("foundAmount: {}", foundAmount);

        // 결제 검증
        if (paymentData.getAmount() != foundAmount.intValue()) {
            log.warn("결제금액 불일치. 위/변조된 결제. 결제 된 금액: {}, 결제 되어야하는 금액: {}", paymentData.getAmount(), foundAmount.intValue());
            // todo 결제 취소 연동

            paymentMapper.updateCancelPayment(foundPayment.getId(), paymentData.toCanceledPayment());
            return;
        }

        paymentMapper.updateCompletePayment(foundPayment.getId(), paymentData.toCompletePayment());
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
        log.info("Start getPayments. username: {}", username);

        int limit = size;
        int offset = (page - 1) * size;

        return paymentMapper.findByUsername(username, offset, limit);
    }

    public int countPaymentByUsername(String username) {
        log.info("Start countPaymentByUsername. username: {}", username);

        return paymentMapper.countByBuyerName(username);
    }

    private Payment getPaymentByMerchantUid(String merchantUid) {
        log.info("Start getPaymentByMerchantUid. merchantUid: {}", merchantUid);

        Optional<Payment> optionalPayment = paymentMapper.findByMerchantUid(merchantUid);
        return optionalPayment.orElseThrow(() -> {
            log.info("merchantUid = {}", merchantUid);
            return new DataNotFoundException("조회한 결제 정보가 없음");
        });
    }

    private void isDuplicatedMerchantUid(String merchantUid) {
        log.info("Start isDuplicatedMerchantUid.");

        boolean result = paymentMapper.isAlreadyExistentMerchantUid(merchantUid);
        if (result) {
            log.warn("merchantUid 중복. merchantUid: {}", merchantUid);
            throw new DuplicateDataException("중복결제 시도");
        }
    }

    private void checkMerchantUidNotNull(String merchantUid) {
        if (merchantUid == null) {
            log.info("merchantUid is null");
            throw new IllegalArgumentException();
        }
    }

    private String getToken() {
        String apiUrl = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"imp_key\": \"%s\", \"imp_secret\": \"%s\"}", apiKey, apiSecret);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

        try {
            String responseBody = responseEntity.getBody();
            log.info("Token Response: {}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String accessToken = jsonNode.path("response").path("access_token").asText();
            return accessToken;
        } catch (Exception e) {
            log.error("API Request failed with status code: {}", responseEntity.getStatusCodeValue());
            return null;
        }
    }

    private void callPrepareApi(String merchantUid, BigDecimal amount, String apiUrl) {
        String accessToken = getToken();
        log.info("Access Token: {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String paymentRequest = String.format("{\"merchant_uid\": \"%s\", \"amount\": %.2f}", merchantUid, amount);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(paymentRequest, headers),
                String.class);

        handleFailedResponse(response);
    }

    private void handleFailedResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Payment request failed with status code: {}", response.getStatusCodeValue());
            throw new RuntimeException("Payment request failed");
        }

        String responseBody = response.getBody();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse JSON response", e);
        }

        JsonNode merchantUidNode = jsonNode.path("response").path("merchant_uid");
        if (merchantUidNode.isNull()) {
            log.error("merchant_uid is null in the payment response");
            throw new RuntimeException("Payment response contains null merchant_uid");
        }
    }

    private void savePreValidationDataToDB(String merchantUid, BigDecimal amount) {
        Payment preparePayment = Payment.builder()
                .status(PaymentStatus.PREPARE)
                .merchantUid(merchantUid)
                .amount(amount)
                .build();

        paymentMapper.insertPrepareRequestData(preparePayment);

        log.info("Save PrepareRequestData. merchantUid: {}", merchantUid);
    }

    private PaymentData fetchPaymentFromIamportServer(String impUid) {
        try {
            PaymentDataResponse responseBody = executeGetPaymentApiCall(impUid);

            log.info("responseBody.getCode(): {}", responseBody.getCode());
            log.info("responseBody.getMessage(): {}", responseBody.getMessage());
            log.info("responseBody.getResponse().getImp_uid(): {}", responseBody.getResponse().getImp_uid());
            log.info("responseBody.getResponse().getMerchant_uid(): {}", responseBody.getResponse().getMerchant_uid());

            return responseBody.getResponse();
        } catch (RestClientException | NullPointerException e) {
            log.error("Exception occurred: {}", e.getMessage());
            throw new IllegalStateException("Failed to retrieve payment data.", e);
        }
    }

    private PaymentDataResponse executeGetPaymentApiCall(String impUid) {
        String url = "https://api.iamport.kr/payments/" + impUid;

        String accessToken = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        log.info("Start API call");
        ResponseEntity<PaymentDataResponse> paymentDataResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentDataResponse.class);
        log.info("Success API call");

        return Objects.requireNonNull(paymentDataResponse.getBody(), "paymentDataResponse is null");
    }
}
