package flab.commercemarket.controller.payment;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.payment.dto.PaymentPostVerificationDto;
import flab.commercemarket.controller.payment.dto.PaymentResponseDto;
import flab.commercemarket.domain.payment.PaymentService;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.controller.payment.dto.PaymentPreVerificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public void preparePayment(@RequestBody PaymentPreVerificationDto request) {
        paymentService.preparePayment(request.getOrderId());
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPayment(@PathVariable long paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return payment.toPaymentResponseDto();
    }

    @GetMapping
    public PageResponseDto<PaymentResponseDto> getPayments(@RequestParam int page, @RequestParam int size) {
        List<Payment> payments = paymentService.getPayments(page, size);

        List<PaymentResponseDto> paymentResponseDto = payments.stream()
                .map(Payment::toPaymentResponseDto)
                .collect(Collectors.toList());

        long totalElements = paymentService.countPayments();

        return PageResponseDto.<PaymentResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .content(paymentResponseDto)
                .build();
    }

}
