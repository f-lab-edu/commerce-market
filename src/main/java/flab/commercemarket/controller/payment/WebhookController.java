package flab.commercemarket.controller.payment;

import flab.commercemarket.controller.payment.dto.PaymentResponseDto;
import flab.commercemarket.controller.payment.dto.PaymentWebhookDto;
import flab.commercemarket.domain.payment.PaymentService;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;

    // todo ec2 배포 이후 해당 컨트롤러를 PaymentController로 옮겨야합니다 -> ngrok 사용으로 "/payments" 엔드포인트를 열지 못함
    @PostMapping
    public PaymentResponseDto webhook(@RequestBody PaymentWebhookDto request) {
        Payment payment = paymentService.processPayment(request.getMerchantUid(), request.getImpUid(), request.getStatus());
        return payment.toPaymentResponseDto();
    }
}
