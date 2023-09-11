package flab.commercemarket.order;

import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.common.utils.DateUtils;
import flab.commercemarket.controller.order.dto.OrderProductRequestDto;
import flab.commercemarket.controller.order.dto.OrderRequestDto;
import flab.commercemarket.controller.order.dto.OrderResponseDto;
import flab.commercemarket.domain.order.OrderService;
import flab.commercemarket.domain.order.repository.OrderRepository;
import flab.commercemarket.domain.order.vo.Order;
import flab.commercemarket.domain.order.vo.OrderProduct;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private AuthorizationHelper authorizationHelper;

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

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerOrderTest() throws Exception {
        // given
        long userId = 1L;
        User user = User.builder().build();
        user.setId(userId);

        // 주문 상품 목록 생성
        List<OrderProductRequestDto> orderProductRequestDtos = createSampleOrderProductRequestDto();
        OrderRequestDto orderRequestDto = new OrderRequestDto(userId, "Hello", orderProductRequestDtos);

        // userService.getUserById() 목 객체 설정
        when(userService.getUserById(userId)).thenReturn(user);

        // productService.getProduct() 목 객체 설정
        Product product = Product.builder().price(5000).build();
        when(productService.getProduct(anyLong())).thenReturn(product);

        // orderRepository.save() 목 객체 설정
        Order savedOrder = Order.builder()
                .id(100L)
                .user(user)
                .orderProduct(createSampleOrderProduct(product, orderProductRequestDtos))
                .requestMessage("Hello")
                .orderedAt(LocalDateTime.MIN)
                .orderPrice(BigDecimal.valueOf(10000.0))
                .build();
        when(orderRepository.save(any())).thenReturn(savedOrder);

        // when
        Order actualOrder = orderService.registerOrder(userId, orderRequestDto);

        // then
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getUser().getId()).isEqualTo(userId);
    }

    @Test
    public void getOrderTest() throws Exception {
        long orderId = 1L;
        Order existingOrder = createSampleOrder(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order order = orderService.getOrder(orderId);

        assertThat(order.getId()).isEqualTo(existingOrder.getId());
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

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Order> mockOrders = new ArrayList<>();

        when(orderRepository.findBetweenDateTime(startDateTime, endDateTime, pageable)).thenReturn(mockOrders);

        // when
        List<Order> result = orderService.getOrderByDate(startDate, endDate, page, size);

        // then
        assertThat(result).isEqualTo(mockOrders);
        verify(orderRepository, times(1)).findBetweenDateTime(startDateTime, endDateTime, pageable);
    }

    @Test
    public void countOrderByDateTest() {
        // given
        String startDate = "2023-09-01";
        String endDate = "2023-09-30";

        LocalDateTime startDateTime = dateUtils.parseDateTime(startDate);
        LocalDateTime  endDateTime = dateUtils.parseDateTime(endDate);

        long mockCount = 10;
        when(orderRepository.countOrderBetweenDate(startDateTime, endDateTime)).thenReturn(mockCount);

        // when
        long result = orderService.countOrderByDate(startDate, endDate);

        // then
        assertThat(result).isEqualTo(mockCount);
        verify(orderRepository, times(1)).countOrderBetweenDate(startDateTime, endDateTime);
    }

    @Test
    public void deleteOrderTest() {
        // given
        long orderId = 123;
        long ownerId = 456;

        User user = User.builder().build();
        user.setId(ownerId);
        Order order = Order.builder().id(1L).user(user).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(authorizationHelper).checkUserAuthorization(order.getUserId(), ownerId);

        // when
        orderService.deleteOrder(orderId, ownerId);

        // then
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    public void deleteOrderTest_forbidden() {
        // given
        long orderId = 123;
        long loginUserId = 456;
        long ownerId = 456;

        User user = User.builder().build();
        user.setId(ownerId);
        Order order = Order.builder().id(1L).user(user).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doThrow(new ForbiddenException("유저 권한 정보가 일치하지 않음"))
                .when(authorizationHelper)
                .checkUserAuthorization(ownerId, loginUserId);

        // when
        assertThrows(ForbiddenException.class, () -> {
            orderService.deleteOrder(orderId, loginUserId);
        });

        // then
        verify(orderRepository, times(0)).delete(order);
    }

    private Order createSampleOrder(long orderId) {
        User user = User.builder()
                .username("sampleUser")
                .build();

        user.setId(1L);

        OrderProduct orderProduct = OrderProduct.builder()
                .id(1L)
                .product(createSampleProduct(1L)) // 임의의 상품(Product) 객체 생성
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200.0))
                .build();

        return Order.builder()
                .id(orderId)
                .user(user)
                .orderProduct(Collections.singletonList(orderProduct))
                .requestMessage("Sample order request")
                .orderedAt(LocalDateTime.now())
                .orderPrice(BigDecimal.valueOf(200.0))
                .build();
    }

    private Product createSampleProduct(long productId) {
        return Product.builder()
                .id(productId)
                .name("Sample Product")
                .price(100)
                .build();
    }

    private List<OrderProduct> createSampleOrderProduct(Product product, List<OrderProductRequestDto> requestDtos) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductRequestDto requestDto : requestDtos) {
            BigDecimal totalPrice = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(requestDto.getQuantity()));
            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(requestDto.getQuantity())
                    .totalPrice(totalPrice)
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
