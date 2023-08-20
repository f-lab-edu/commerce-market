package flab.commercemarket.domain.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.payment.mapper.PaymentMapper;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    public String getToken() {
        String apiUrl = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"imp_key\": \"%s\", \"imp_secret\": \"%s\"}", apiKey, apiSecret);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        String responseBody = responseEntity.getBody();
        log.info("responseEntity.getBody() = {}", responseBody);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode accessTokenNode = responseJson.get("response").get("access_token");
            return accessTokenNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


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
