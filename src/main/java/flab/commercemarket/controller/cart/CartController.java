package flab.commercemarket.controller.cart;

import flab.commercemarket.controller.cart.dto.CartDto;
import flab.commercemarket.controller.cart.dto.CartResponseDto;
import flab.commercemarket.domain.cart.CartService;
import flab.commercemarket.domain.cart.vo.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponseDto postCart(@RequestBody CartDto cartDto) {
        Cart data = cartDto.toCart();
        long loginUserId = data.getUserId(); // todo 토큰에서 조회하도록 변경

        Cart registerCart = cartService.registerCart(data, loginUserId);
        return registerCart.toCartResponseDto();
    }

    @PatchMapping("/{cartId}")
    public CartResponseDto patchCart(@PathVariable("cartId") long cartId,
                                     @RequestBody CartDto cartDto) {
        // todo 로그인 기능 추가 이후 해당 장바구니 수정에 대한 권한 검증로직이 추가되어야 합니다.
        Cart data = cartDto.toCart();
        long userId = data.getUserId(); // todo 토큰에서 조회하도록 변경

        Cart updatedCart = cartService.updateCart(data, cartId, userId);
        return updatedCart.toCartResponseDto();
    }

    /*
     * @param userId
     * @param page
     * @return 특정 사용자의 장바구니 리스트
     */
    @GetMapping
    public List<CartResponseDto> getCarts(@RequestParam long userId) {
        // todo 로그인 기능 추가 이후 userId를 조회하는 로직 변경 -> 토큰에서 조회 등
        List<Cart> carts = cartService.findCarts(userId);
        return carts.stream()
                .map(Cart::toCartResponseDto)
                .collect(Collectors.toList());
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


