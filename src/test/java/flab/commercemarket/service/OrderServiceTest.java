package flab.commercemarket.service;

import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.utils.DateUtils;
import flab.commercemarket.controller.order.dto.OrderProductRequestDto;
import flab.commercemarket.controller.order.dto.OrderRequestDto;
import flab.commercemarket.domain.order.OrderService;
import flab.commercemarket.domain.order.repository.OrderRepository;
import flab.commercemarket.domain.order.vo.Order;
import flab.commercemarket.domain.order.vo.OrderProduct;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DateUtils dateUtils;

    @InjectMocks
    private OrderService orderService;

    long userId = 1L;
    long productId = 1L;
    User user;
    Product product;
    String email;

    @BeforeEach
    void init() {
        user = User.builder().id(userId).build();
        product = Product.builder().id(productId).name("product name").price(100).build();
        email = "abc@gmail.com";
    }

    @Test
    public void registerOrderTest() throws Exception {
        // given
        List<OrderProductRequestDto> orderProductRequestDtos = createSampleOrderProductRequestDto();
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderProductRequestDtos);

        when(userService.getUserByEmail(email)).thenReturn(user);
        Product product = Product.builder().price(5000).build();
        when(productService.getProductById(anyLong())).thenReturn(product);

        Order savedOrder = Order.builder()
                .id(100L)
                .user(user)
                .orderProduct(createSampleOrderProduct(product, orderProductRequestDtos))
                .orderedAt(LocalDateTime.MIN)
                .orderPrice(BigDecimal.valueOf(10000.0))
                .build();
        when(orderRepository.save(any())).thenReturn(savedOrder);

        // when
        Order actualOrder = orderService.registerOrder(email, orderRequestDto);

        // then
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getUser().getId()).isEqualTo(userId);
    }

    @Test
    public void getOrderTest() throws Exception {
        long orderId = 1L;
        Order existingOrder = orderFixture(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order order = orderService.getOrder(orderId);

        assertThat(order.getId()).isEqualTo(existingOrder.getId());
    }

    @Test
    public void deleteOrderTest() {
        // given
        long orderId = 123;
        Order order = Order.builder().id(orderId).user(user).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.deleteOrder(email, orderId);

        // then
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    public void deleteOrderTest_Forbidden_Exception() {
        // given
        long unauthorizedUserId = 99;
        User forbiddenUser = User.builder().id(unauthorizedUserId).role(Role.USER).build();

        long orderId = 1L;
        Order order = Order.builder().id(orderId).user(user).build();
        when(userService.getUserByEmail(email)).thenReturn(forbiddenUser);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // then
        assertThrows(ForbiddenException.class, () ->
                orderService.deleteOrder(email, orderId));
    }

    @Test
    public void getOrderByDateTest() {
        // given
        String startDate = "2023-09-01";
        String endDate = "2023-09-30";
        int page = 1;
        int size = 10;

        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate);
        LocalDateTime  endDateTime = dateUtils.parseDateTime(endDate);
        when(userService.getUserByEmail(email)).thenReturn(user);

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Order> mockOrders = new ArrayList<>();

        when(orderRepository.findBetweenDateTime(user.getId(), startDateTime, endDateTime, pageable)).thenReturn(mockOrders);

        // when
        List<Order> result = orderService.getOrderByDate(email, startDate, endDate, page, size);

        // then
        assertThat(result).isEqualTo(mockOrders);
        verify(orderRepository, times(1)).findBetweenDateTime(user.getId(), startDateTime, endDateTime, pageable);
    }

    @Test
    public void countOrderByDateTest() {
        // given
        String startDate = "2023-09-01";
        String endDate = "2023-09-30";

        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate);
        LocalDateTime  endDateTime = dateUtils.parseDateTime(endDate);

        long mockCount = 10;
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(orderRepository.countOrderBetweenDate(user.getId(), startDateTime, endDateTime)).thenReturn(mockCount);

        // when
        long result = orderService.countOrderByDate(email, startDate, endDate);

        // then
        assertThat(result).isEqualTo(mockCount);
        verify(orderRepository, times(1)).countOrderBetweenDate(user.getId(), startDateTime, endDateTime);
    }

    private Order orderFixture(long orderId) {
        OrderProduct orderProduct = OrderProduct.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        return Order.builder()
                .id(orderId)
                .user(user)
                .orderProduct(Collections.singletonList(orderProduct))
                .orderedAt(LocalDateTime.now())
                .orderPrice(BigDecimal.valueOf(200.0))
                .build();
    }

    private List<OrderProduct> createSampleOrderProduct(Product product, List<OrderProductRequestDto> requestDtos) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductRequestDto requestDto : requestDtos) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(requestDto.getQuantity())
                    .build();
            orderProducts.add(orderProduct);
        }
        return orderProducts;
    }

    private List<OrderProductRequestDto> createSampleOrderProductRequestDto() {
        List<OrderProductRequestDto> productDtos = new ArrayList<>();

        // 첫 번째 주문 상품 설정
        OrderProductRequestDto productDto1 = new OrderProductRequestDto();
        productDtos.add(productDto1);

        // 두 번째 주문 상품 설정
        OrderProductRequestDto productDto2 = new OrderProductRequestDto();
        productDtos.add(productDto2);

        return productDtos;
    }
}
