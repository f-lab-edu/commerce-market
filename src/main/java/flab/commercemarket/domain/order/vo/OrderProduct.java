package flab.commercemarket.domain.order.vo;

import flab.commercemarket.controller.order.dto.OrderProductDto;
import flab.commercemarket.domain.product.vo.Product;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "idx_order_id", columnList = "order_id")
})
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public OrderProductDto toOrderProductDto() {
        return OrderProductDto.builder()
                .id(id)
                .productResponseDto(product.toProductResponseDto())
                .quantity(quantity)
                .build();
    }
}
