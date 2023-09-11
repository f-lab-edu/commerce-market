package flab.commercemarket.controller.order;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.order.dto.OrderRequestDto;
import flab.commercemarket.controller.order.dto.OrderResponseDto;
import flab.commercemarket.domain.order.OrderService;
import flab.commercemarket.domain.order.vo.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto postOrder(@RequestParam long loginUserId, @RequestBody OrderRequestDto orderRequestDto) {
        Order order = orderService.registerOrder(loginUserId, orderRequestDto);
        return order.toOrderResponseDto();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrder(@PathVariable long orderId) {
        Order order = orderService.getOrder(orderId);
        return order.toOrderResponseDto();
    }

    @GetMapping
    public PageResponseDto<OrderResponseDto> getOrderDateRange(@RequestParam String startDate,
                                                               @RequestParam String endDate,
                                                               @RequestParam int page,
                                                               @RequestParam int size) {

        List<OrderResponseDto> contents = orderService.getOrderByDate(startDate, endDate, page, size)
                .stream()
                .map(Order::toOrderResponseDto)
                .collect(Collectors.toList());

        long totalElements = orderService.countOrderByDate(startDate, endDate);

        return PageResponseDto.<OrderResponseDto>builder()
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
