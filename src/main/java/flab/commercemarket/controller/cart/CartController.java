package flab.commercemarket.controller.cart;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.cart.dto.CartDto;
import flab.commercemarket.controller.cart.dto.CartResponseDto;
import flab.commercemarket.domain.cart.CartService;
import flab.commercemarket.domain.cart.vo.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponseDto postCart(@RequestBody CartDto cartDto) {

        long loginUserId = cartDto.getUserId(); // todo 토큰에서 조회하도록 변경

        Cart registerCart = cartService.registerCart(cartDto, loginUserId);
        return registerCart.toCartResponseDto();
    }

    @PatchMapping("/{cartId}")
    public CartResponseDto patchCart(@PathVariable("cartId") long cartId,
                                     @RequestBody CartDto cartDto) {
        // todo 로그인 기능 추가 이후 해당 장바구니 수정에 대한 권한 검증로직이 추가되어야 합니다.

        long userId = cartDto.getUserId(); // todo 토큰에서 조회하도록 변경

        Cart updatedCart = cartService.updateCart(cartDto, cartId, userId);
        return updatedCart.toCartResponseDto();
    }

    @GetMapping
    public PageResponseDto<CartResponseDto> getCarts(@RequestParam long userId, @RequestParam int page, @RequestParam int size) {
        // todo 로그인 기능 추가 이후 userId를 조회하는 로직 변경 -> 토큰에서 조회 등
        List<Cart> carts = cartService.findCarts(userId, page, size);

        List<CartResponseDto> cartResponseDto = carts.stream()
                .map(Cart::toCartResponseDto)
                .collect(Collectors.toList());

        long totalElements = cartService.countCartByUserId(userId);

        return PageResponseDto.<CartResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .content(cartResponseDto)
                .build();
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable long cartId) {
        // todo 로그인 기능 추가 이후 해당 장바구니 삭제에 대한 권한 검증로직이 추가되어야 합니다.
        long loginUserId = 1L; // 토큰에서 조회하는 로직으로 변경해야함
        cartService.deleteCart(cartId, loginUserId);
    }

    @GetMapping("/{userId}")
    public int getPrice(@PathVariable long userId) {
        // todo 로그인 기능 추가 이후 userId를 조회하는 로직 변경
        return cartService.calculateTotalPrice(userId);
    }
}


