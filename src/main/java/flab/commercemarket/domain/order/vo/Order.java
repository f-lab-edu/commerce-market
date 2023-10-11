package flab.commercemarket.domain.order.vo;

import flab.commercemarket.controller.order.dto.OrderResponseDto;
import flab.commercemarket.domain.user.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "MARKET_ORDER")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_order_id", columnList = "id"))
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private List<OrderProduct> orderProduct;

    private String requestMessage;

    private LocalDateTime orderedAt;

    private BigDecimal orderPrice;

    @Column(unique = true)
    private String merchantUid;

    public OrderResponseDto toOrderResponseDto() {
        return OrderResponseDto.builder()
                .id(id)
                .userId(user.getId())
                .orderProduct(orderProduct)
                .requestMessage(requestMessage)
                .orderedAt(orderedAt)
                .orderPrice(orderPrice)
                .merchantUid(merchantUid)
                .build();
    }

    public long getUserId() {
        return user.getId();
    }
}
