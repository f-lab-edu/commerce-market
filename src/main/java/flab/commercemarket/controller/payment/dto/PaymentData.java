package flab.commercemarket.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import flab.commercemarket.domain.payment.vo.Payment;
import flab.commercemarket.domain.payment.vo.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentData {
    private String imp_uid;
    private String merchant_uid;
    private String pay_method;
    private String channel;
    private String pg_provider;
    private String emb_pg_provider;
    private String pg_tid;
    private String pg_id;
    private boolean escrow;
    private String apply_num;
    private String bank_code;
    private String bank_name;
    private String card_code;
    private String card_name;
    private int card_quota;
    private String card_number;
    private String card_type;
    private String vbank_code;
    private String vbank_name;
    private String vbank_num;
    private String vbank_holder;
    private long vbank_date;
    private long vbank_issued_at;
    private String name;
    private int amount;
    private int cancel_amount;
    private String currency;
    private String buyer_name;
    private String buyer_email;
    private String buyer_tel;
    private String buyer_addr;
    private String buyer_postcode;
    private String custom_data;
    private String user_agent;
    private String status;
    private long started_at;
    private long paid_at;
    private long failed_at;
    private long cancelled_at;
    private String fail_reason;
    private String cancel_reason;
    private String receipt_url;
    private List<CancelHistory> cancel_history;
    private List<String> cancel_receipt_urls;
    private boolean cash_receipt_issued;
    private String customer_uid;
    private String customer_uid_usage;

    public Payment toCompletePayment() {
        return Payment.builder()
                .impUid(imp_uid)
                .payMethod(pay_method)
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .receiptUrl(receipt_url)
                .pgProvider(pg_provider)
                .buyerName(buyer_name)
                .success(true)
                .build();
    }

    public Payment toCanceledPayment() {
        return Payment.builder()
                .impUid(imp_uid)
                .payMethod(pay_method)
                .status(PaymentStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .receiptUrl(receipt_url)
                .pgProvider(pg_provider)
                .buyerName(buyer_name)
                .success(true)
                .build();
    }
}
