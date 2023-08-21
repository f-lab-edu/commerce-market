package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestDto {
    @JsonProperty("merchant_uid")
    private String merchantUid;
    private double amount;
}
