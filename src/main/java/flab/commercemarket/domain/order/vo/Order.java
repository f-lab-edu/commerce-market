package flab.commercemarket.domain.order.vo;

import flab.commercemarket.controller.order.dto.OrderGetResponseDto;
import flab.commercemarket.controller.order.dto.OrderResponseDto;
import flab.commercemarket.domain.user.vo.User;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @BatchSize(size = 5)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private List<OrderProduct> orderProduct;

    private LocalDateTime orderedAt;

    private BigDecimal orderPrice;

    @Column(unique = true)
    private String merchantUid;

    public OrderResponseDto toOrderResponseDto() {
        return OrderResponseDto.builder()
                .id(id)
                .user(user)
                .orderProduct(orderProduct)
                .orderedAt(orderedAt)
                .orderPrice(orderPrice)
                .merchantUid(merchantUid)
                .build();
    }

    public OrderGetResponseDto toOrderGetResponseDto() {
        return OrderGetResponseDto.builder()
                .id(id)
                .user(user.toUserResponseDto())
                .orderProductDto(orderProduct.stream().map(OrderProduct::toOrderProductDto).collect(Collectors.toList()))
                .orderedAt(orderedAt)
                .orderPrice(orderPrice)
                .merchantUid(merchantUid)
                .build();
    }

    public long getUserId() {
        return user.getId();
    }
}
