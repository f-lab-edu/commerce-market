package flab.commercemarket.controller.payment;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.payment.dto.PaymentDto;
import flab.commercemarket.controller.payment.dto.PaymentResponseDto;
import flab.commercemarket.domain.payment.PaymentService;
import flab.commercemarket.domain.payment.vo.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponseDto postPayment(@RequestBody PaymentDto paymentDto) {
        Payment payment = paymentDto.toPayment();
        Payment createdPayment = paymentService.processPayment(payment);
        return createdPayment.toPaymentResponseDto();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPayment(@PathVariable long paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return payment.toPaymentResponseDto();
    }

    @GetMapping
    public PageResponseDto<PaymentResponseDto> getPayments(@RequestParam String username, @RequestParam int page, @RequestParam int size) {
        List<Payment> payments = paymentService.getPayments(username, page, size);

        int totalElements = paymentService.countPaymentByUsername(username);
        List<PaymentResponseDto> paymentResponseDto = payments.stream()
                .map(Payment::toPaymentResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<PaymentResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .content(paymentResponseDto)
                .build();
    }

}
