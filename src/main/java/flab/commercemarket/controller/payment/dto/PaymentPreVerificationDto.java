package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentPreVerificationDto {

    @JsonProperty("merchant_uid")
    private String merchantUid;
    private BigDecimal amount;
}
