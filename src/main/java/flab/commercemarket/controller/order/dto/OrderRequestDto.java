package flab.commercemarket.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private long buyerId;
    private String requestMessage;
    private List<OrderProductRequestDto> products;
}

