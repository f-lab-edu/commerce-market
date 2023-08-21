package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelHistory {
    private String pg_tid;
    private int amount;
    private long cancelled_at;
    private String reason;
    private String receipt_url;
}
