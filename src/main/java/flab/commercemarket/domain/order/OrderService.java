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
    public Order registerOrder(long loginUserId, OrderRequestDto orderRequestDto) {
        log.info("Start registerOrder");
        authorizationHelper.checkUserAuthorization(orderRequestDto.getBuyerId(), loginUserId);
        User buyer = userService.getUserById(orderRequestDto.getBuyerId());
        List<OrderProduct> orderProductList = createOrderProductList(orderRequestDto);

        LocalDateTime orderedAt = LocalDateTime.now();
        String merchantUid = merchantUidBuilder(loginUserId, orderedAt);

        BigDecimal orderPrice = orderProductList.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("주문 금액: {}", orderPrice);

        Order order = Order.builder()
                .user(buyer)
                .orderProduct(orderProductList)
                .requestMessage(orderRequestDto.getRequestMessage())
                .orderedAt(orderedAt)
                .orderPrice(orderPrice)
                .merchantUid(merchantUid)
                .build();

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(long orderId) {
        log.info("Start getOrder. orderId: {}", orderId);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return optionalOrder.orElseThrow(() -> {
            log.info("orderId: {}", orderId);
            return new DataNotFoundException("조회한 주문정보가 없음");
        });
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
        authorizationHelper.checkUserAuthorization(order.getUserId(), loginUserId);
        orderRepository.delete(order);
    }

    private List<OrderProduct> createOrderProductList(OrderRequestDto orderRequestDto) {
        log.info("Start createOrderProductList.");

        return orderRequestDto.getProducts()
                .stream()
                .map(orderProductRequestDto -> {
                    Product foundProduct = productService.getProduct(orderProductRequestDto.getProductId());
                    int quantity = orderProductRequestDto.getQuantity();
                    BigDecimal productPrice = BigDecimal.valueOf(foundProduct.getPrice());
                    BigDecimal productTotalPrice = productPrice.multiply(BigDecimal.valueOf(quantity));

                    return OrderProduct.builder()
                            .product(foundProduct)
                            .quantity(quantity)
                            .totalPrice(productTotalPrice)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Order> getOrderByDate(String startDate, String endDate, int page, int size) {
        log.info("Start getOrderByDate.");
        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate + "T00:00:00");
        LocalDateTime endDateTime = dateUtils.parseDateTime(endDate + "T23:59:59");
        log.info("Parse String to LocalDateTime. startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        Pageable pageable = PageRequest.of(page - 1, size);
        return orderRepository.findBetweenDateTime(startDateTime, endDateTime, pageable);
    }

    @Transactional(readOnly = true)
    public long countOrderByDate(String startDate, String endDate) {
        log.info("Start countOrderByDate.");
        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate + "T00:00:00");
        LocalDateTime endDateTime = dateUtils.parseDateTime(endDate + "T23:59:59");
        log.info("Parse String to LocalDateTime. startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        return orderRepository.countOrderBetweenDate(startDateTime, endDateTime);
    }

    // PG 사에서 사용하는 주문 고유 번호. 유니크한 값이어야함
    private String merchantUidBuilder(long loginUserId, LocalDateTime orderedAt) {
        int mills = orderedAt.getNano();
        return String.format("merch_%03d_%d", mills, loginUserId);
    }
}
