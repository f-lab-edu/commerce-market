package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDataResponseDto extends PaymentResponseDataDto {
    private int code;
    private String message;
    private PaymentResponseDataDto response;
}
