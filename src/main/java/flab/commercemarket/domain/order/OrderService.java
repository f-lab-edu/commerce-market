package flab.commercemarket.domain.order;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.controller.order.dto.OrderRequestDto;
import flab.commercemarket.domain.order.repository.OrderRepository;
import flab.commercemarket.common.utils.DateUtils;
import flab.commercemarket.domain.order.vo.Order;
import flab.commercemarket.domain.order.vo.OrderProduct;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final AuthorizationHelper authorizationHelper;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final DateUtils dateUtils;

    @Transactional
    public Order registerOrder(String email, OrderRequestDto orderRequestDto) {
        log.info("Start registerOrder");
        User buyer = userService.getUserByEmail(email);
        List<OrderProduct> orderProductList = createOrderProductList(orderRequestDto);
        LocalDateTime orderedAt = LocalDateTime.now();
        String merchantUid = merchantUidBuilder(buyer.getId(), orderedAt);
        BigDecimal orderPrice = calculateOrderPrice(orderRequestDto);

        return orderRepository.save(Order.builder()
                .user(buyer)
                .orderProduct(orderProductList)
                .orderedAt(orderedAt)
                .orderPrice(orderPrice)
                .merchantUid(merchantUid)
                .build());
    }

    public Order getOrder(long orderId) {
        log.info("Start getOrder. orderId: {}", orderId);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() -> {
            log.info("orderId: {}", orderId);
            return new DataNotFoundException("조회한 주문정보가 없음");
        });

        log.info("order: {}", order);
        return order;
    }

    public Order getOrderByMerchantUid(String merchantUid) {
        log.info("getOrderByMerchantUid(): merchantUid: {}", merchantUid);
        return orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new DataNotFoundException("조회한 주문정보가 없음"));
    }

    @Transactional
    public void deleteOrder(long orderId, long loginUserId) {
        log.info("Start deleteOrder. orderId: {}", orderId);

        Order order = getOrder(orderId);
        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrderByDate(String email, String startDate, String endDate, int page, int size) {
        log.info("Start getOrderByDate.");
        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate + "T00:00:00");
        LocalDateTime endDateTime = dateUtils.parseDateTime(endDate + "T23:59:59");
        log.info("Parse String to LocalDateTime. startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        Pageable pageable = PageRequest.of(page - 1, size);
        User foundUser = userService.getUserByEmail(email);
        return orderRepository.findBetweenDateTime(foundUser.getId(), startDateTime, endDateTime, pageable);
    }

    @Transactional(readOnly = true)
    public long countOrderByDate(String email, String startDate, String endDate) {
        log.info("Start countOrderByDate.");
        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate + "T00:00:00");
        LocalDateTime endDateTime = dateUtils.parseDateTime(endDate + "T23:59:59");
        log.info("Parse String to LocalDateTime. startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        User foundUser = userService.getUserByEmail(email);
        return orderRepository.countOrderBetweenDate(foundUser.getId(), startDateTime, endDateTime);
    }

    // PG 사에서 사용하는 주문 고유 번호. 유니크한 값이어야함
    private String merchantUidBuilder(long loginUserId, LocalDateTime orderedAt) {
        int mills = orderedAt.getNano();
        return String.format("merch_%03d_%d", mills, loginUserId);
    }

    private BigDecimal calculateOrderPrice(OrderRequestDto orderRequestDto) {
        BigDecimal orderPrice = orderRequestDto.getProducts().stream()
                .map(product -> {
                    long productId = product.getProductId();
                    Product foundProduct = productService.getProductById(productId);
                    return BigDecimal.valueOf(product.getQuantity()).multiply(BigDecimal.valueOf(foundProduct.getPrice()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return orderPrice;
    }

    private List<OrderProduct> createOrderProductList(OrderRequestDto orderRequestDto) {
        return orderRequestDto.getProducts()
                .stream()
                .map(orderProductRequestDto -> {
                    Product foundProduct = productService.getProductById(orderProductRequestDto.getProductId());
                    int quantity = orderProductRequestDto.getQuantity();
                    return OrderProduct.builder()
                            .product(foundProduct)
                            .quantity(quantity)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
