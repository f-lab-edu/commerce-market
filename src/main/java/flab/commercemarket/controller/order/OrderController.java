package flab.commercemarket.controller.order;

import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.order.dto.OrderGetResponseDto;
import flab.commercemarket.controller.order.dto.OrderRequestDto;
import flab.commercemarket.controller.order.dto.OrderResponseDto;
import flab.commercemarket.domain.order.OrderService;
import flab.commercemarket.domain.order.vo.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
public class OrderController {
    private final OrderService orderService;
    private final AuthorizationHelper authorizationHelper;

    @PostMapping
    public OrderResponseDto postOrder(@RequestBody OrderRequestDto orderRequestDto) {
        String email = authorizationHelper.getPrincipalEmail();
        Order order = orderService.registerOrder(email, orderRequestDto);
        return order.toOrderResponseDto();
    }

    @GetMapping("/{orderId}")
    public OrderGetResponseDto getOrder(@PathVariable long orderId) {
        Order order = orderService.getOrder(orderId);

        return order.toOrderGetResponseDto();
    }

    @GetMapping
    public PageResponseDto<OrderGetResponseDto> getOrderDateRange(@RequestParam String startDate, @RequestParam String endDate, @RequestParam int page, @RequestParam int size) {

        String email = authorizationHelper.getPrincipalEmail();
        List<OrderGetResponseDto> contents = orderService.getOrderByDate(email, startDate, endDate, page, size)
                .stream()
                .map(Order::toOrderGetResponseDto)
                .collect(Collectors.toList());

        long totalElements = orderService.countOrderByDate(email, startDate, endDate);

        return PageResponseDto.<OrderGetResponseDto>builder()
                .size(size)
                .page(page)
                .content(contents)
                .totalElements(totalElements)
                .build();
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable long orderId, @RequestParam long loginUserId) {
        orderService.deleteOrder(orderId, loginUserId);
    }
}
