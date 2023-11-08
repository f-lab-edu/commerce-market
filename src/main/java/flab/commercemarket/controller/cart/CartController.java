package flab.commercemarket.controller.cart;

import flab.commercemarket.common.helper.AuthorizationHelper;
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
    private final AuthorizationHelper authorizationHelper;

    @PostMapping
    public CartResponseDto postCart(@RequestBody CartDto cartDto) {
        Cart registerCart = cartService.registerCart(cartDto, authorizationHelper.getPrincipalEmail());
        return registerCart.toCartResponseDto();
    }

    @PatchMapping("/{cartId}")
    public CartResponseDto patchCart(@PathVariable("cartId") long cartId, @RequestBody CartDto cartDto) {
        Cart updatedCart = cartService.updateCart(cartDto, cartId, authorizationHelper.getPrincipalEmail());
        return updatedCart.toCartResponseDto();
    }

    @GetMapping
    public PageResponseDto<CartResponseDto> getCarts(@RequestParam int page, @RequestParam int size) {
        String principalEmail = authorizationHelper.getPrincipalEmail();
        List<Cart> carts = cartService.findCarts(principalEmail, page, size);

        List<CartResponseDto> cartResponseDto = carts.stream()
                .map(Cart::toCartResponseDto)
                .collect(Collectors.toList());

        long totalElements = cartService.countCartByUserId(principalEmail);

        return PageResponseDto.<CartResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .content(cartResponseDto)
                .build();
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable long cartId) {
        String principalEmail = authorizationHelper.getPrincipalEmail();
        cartService.deleteCart(principalEmail, cartId);
    }

    @GetMapping("/price")
    public int getPrice() {
        String principalEmail = authorizationHelper.getPrincipalEmail();
        return cartService.calculateTotalPrice(principalEmail);
    }
}


