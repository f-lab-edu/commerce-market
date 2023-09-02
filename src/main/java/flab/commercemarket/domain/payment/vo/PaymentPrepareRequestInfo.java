package flab.commercemarket.domain.payment.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepareRequestInfo {
    @JsonProperty("merchant_uid")
    private String merchantUid;
    private BigDecimal amount;
}
