package flab.commercemarket.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.controller.cart.dto.CartDto;
import flab.commercemarket.domain.cart.CartService;
import flab.commercemarket.domain.cart.repository.CartRepository;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    CartRepository cartRepository;

    @Mock
    ProductService productService;

    @Mock
    UserService userService;

    @InjectMocks
    CartService cartService;

    long userId = 1L;
    long productId = 1L;

    CartDto cartDto;
    User user;
    Product product;
    String email;

    @BeforeEach
    void init() {
        user = User.builder().id(userId).role(Role.USER).email("a@gmail.com").build();
        product = Product.builder().id(productId).name("productName").build();
        cartDto = new CartDto(productId, 10);
        email = "aaa@gmail.com";
    }

    @Test
    @DisplayName("장바구니에 상품을 등록한다.")
    public void registerCartTest() throws Exception {
        // given
        long cartId = 100L;
        Cart cart = cartFixture(cartId);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(productService.getProductById(productId)).thenReturn(product);
        when(cartRepository.isAlreadyExistentProductInUserCart(userId, productId)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        Cart registeredCart = cartService.registerCart(cartDto, email);

        // then
        assertNotNull(registeredCart);
        assertThat(cartDto.getProductId()).isEqualTo(registeredCart.getProductId());
    }

    @Test
    @DisplayName("장바구니 등록시 사용자가 존재하지 않으면 예외가 발생한다.")
    public void registerCartTest_notFoundUser() throws Exception {
        // given
        when(userService.getUserByEmail(email)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.registerCart(cartDto, email);
        });
    }

    @Test
    @DisplayName("장바구니 등록 시 상품이 존재하지 않으면 예외가 발생한다.")
    public void registerCartTest_notFoundProduct() throws Exception {
        // given
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(productService.getProductById(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.registerCart(cartDto, email);
        });
    }

    @Test
    public void updateCartTest() throws Exception {
        // given
        long cartId = 10L;
        Cart cart = cartFixture(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserByEmail(email)).thenReturn(user);
        CartDto updateData = new CartDto(productId, 99);

        // when
        Cart updateCart = cartService.updateCart(updateData, cartId, email);

        // then
        assertNotNull(updateCart);
        assertThat(updateData.getProductId()).isEqualTo(updateCart.getProductId());
        assertThat(updateData.getQuantity()).isEqualTo(updateCart.getQuantity());
    }

    @Test
    @DisplayName("로그인 된 사용자 정보와 장바구니 정보의 사용자 정보가 일치하지 않으면 예외를 발생시킨다.")
    public void updateCartTest_ForbiddenException() throws Exception {
        // given
        User forbiddenUser = User.builder().id(999L).role(Role.USER).build();
        long cartId = 10L;
        Cart cart = cartFixture(cartId);
        when(userService.getUserByEmail(email)).thenReturn(forbiddenUser);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        CartDto updateData = new CartDto(productId, 99);

        // then
        assertThrows(ForbiddenException.class, () -> {
            cartService.updateCart(updateData, cartId, email);
        });
    }

    @Test
    public void findCartsTest() throws Exception {
        // given
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        List<Cart> cartList = cartListFixture();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(cartRepository.findCartByUserId(userId, pageable)).thenReturn(cartList.subList(0, size));

        // when
        List<Cart> result = cartService.findCarts(email, page, size);

        // then
        assertThat(result.size()).isEqualTo(size);
    }

    @Test
    public void countCartByUserIdTest() throws Exception {
        // given
        when(cartRepository.countCartByEmail(email)).thenReturn((long) cartListFixture().size());

        // when
        long result = cartService.countCartByUserEmail(email);

        // then
        assertThat(cartListFixture().size()).isEqualTo(result);
    }

    @Test
    public void deleteCartTest() throws Exception {
        // given
        long cartId = 15;
        Cart cart = cartFixture(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserByEmail(email)).thenReturn(user);

        // when
        cartService.deleteCart(email, cartId);

        // then
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    public void calculateTotalPriceTest() {
        // Given
        long userId = 1L;
        Product product1 = Product.builder().id(1L).price(1000).build();
        Product product2 = Product.builder().id(2L).price(500).build();
        List<Cart> cartList = new ArrayList<>();

        Cart cart1 = Cart.builder().product(product1).quantity(3).build();
        Cart cart2 = Cart.builder().product(product2).quantity(2).build();

        cartList.add(cart1);
        cartList.add(cart2);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(cartRepository.findAllByUserId(userId)).thenReturn(cartList);
        when(productService.getProductById(1L)).thenReturn(product1);
        when(productService.getProductById(2L)).thenReturn(product2);

        // When
        int totalPrice = cartService.calculateTotalPrice(email);

        // then
        assertThat(4000).isEqualTo(totalPrice);
    }


    private Cart cartFixture(long cartId) {
        return Cart.builder().id(cartId).user(user).product(product).build();
    }

    private List<Cart> cartListFixture() {
        return IntStream.range(1, 21)
                .mapToObj(this::cartFixture)
                .collect(Collectors.toList());
    }
}
