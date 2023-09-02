package flab.commercemarket.domain.payment.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyInfo {
    @JsonProperty("imp_key")
    private String apiKey;

    @JsonProperty("imp_secret")
    private String apiSecret;
}
