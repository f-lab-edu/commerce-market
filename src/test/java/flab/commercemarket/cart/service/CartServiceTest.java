package flab.commercemarket.cart.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.controller.cart.dto.CartDto;
import flab.commercemarket.domain.cart.CartService;
import flab.commercemarket.domain.cart.repository.CartRepository;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("장바구니 등록 성공")
    public void registerCartTest_successfulRegister() throws Exception {
        // given
        long productId = 1L;
        long userId = 1L;
        CartDto cartDto = new CartDto();
        User user = User.builder().build();
        user.setId(userId);
        Product product = Product.builder().id(productId).build();
        Cart cart = Cart.builder().user(user).product(product).quantity(1).build();

        // Mock 설정
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(productId)).thenReturn(product);
        when(cartRepository.save(any())).thenReturn(cart);

        // when
        Cart expectedCart = cartService.registerCart(cartDto, userId);

        // then
        assertThat(expectedCart.getProductId()).isEqualTo(productId);
        assertThat(expectedCart.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자 정보가 존재하지 않을때 예외를 발생시킨다.")
    public void registerCartTest_not_found_user() throws Exception {
        // given
        CartDto cartDto = new CartDto();
        long userId = 1L;

        // when
        when(userService.getUserById(cartDto.getUserId())).thenThrow(new DataNotFoundException("해당 id의 유저가 없습니다."));

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.registerCart(cartDto, userId);
        });
    }

    @Test
    public void registerCartTest_not_found_product() throws Exception {
        // given
        CartDto cartDto = new CartDto();
        long productId = 1L;

        // when
        when(productService.getProduct(cartDto.getProductId())).thenThrow(new DataNotFoundException("해당 id의 상품 정보가 없습니다."));

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.registerCart(cartDto, productId);
        });
    }

    @Test
    @DisplayName("장바구니에 이미 존재하는 상품을 다시 담으면 예외가 발생한다.")
    public void registerCartTest_duplicate_product() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        CartDto data = new CartDto();
        User user = User.builder().build();
        user.setId(userId);
        Product product = Product.builder().id(productId).build();

        // when
        when(userService.getUserById(data.getUserId())).thenReturn(user);
        when(productService.getProduct(data.getProductId())).thenReturn(product);
        when(cartRepository.isAlreadyExistentProductInUserCart(userId, data.getProductId())).thenThrow(DuplicateDataException.class);

        // then
        assertThrows(DuplicateDataException.class, () -> {
            cartService.registerCart(data, userId);
        });
    }

    @Test
    @DisplayName("업데이할 수량의 정보를 입력받아 정보를 수정해야한다.")
    void updateCartTest_successfulUpdate() {
        // given
        CartDto data = new CartDto();
        long cartId = 1L;
        long userId = 1L;
        long productId = 2L;
        User user = User.builder().build();
        user.setId(userId);
        Product product = Product.builder().id(productId).build();
        Cart existCart = Cart.builder().user(user).product(product).build();

        // when
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(existCart);

        Cart result = cartService.updateCart(new CartDto(), cartId, userId);

        // then
        assertThat(existCart.getId()).isEqualTo(result.getId());
        assertThat(existCart.getUserId()).isEqualTo(result.getUserId());
        assertThat(existCart.getProductId()).isEqualTo(result.getProductId());
    }

    @Test
    @DisplayName("장바구니 정보가 조회되지 않으면 예외를 발생시켜야한다.")
    public void updateCartTest_cartNotFound() throws Exception {
        // given
        long userId = 1L;
        long cartId = 1L;

        // when
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.updateCart(new CartDto(), cartId, userId);
        });
    }

    @Test
    public void findCartsTest() {
        // given
        long userId = 123L;
        int page = 1;
        int size = 2;
        List<Cart> carts = new ArrayList<>();
        carts.add(new Cart());
        carts.add(new Cart());
        carts.add(new Cart());

        when(cartRepository.findCartByUserId(eq(userId), any(Pageable.class))).thenReturn(carts);

        // when
        List<Cart> result = cartService.findCarts(userId, page, size);

        // then
        assertThat(carts.size()).isEqualTo(result.size());
        verify(cartRepository, times(1)).findCartByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    public void countCartByUserIdTest() throws Exception {
        // given
        long userId = 1L;

        // when
        when(cartRepository.countCartByUserId(userId)).thenReturn(100L);

        // then
        assertThat(cartService.countCartByUserId(userId)).isEqualTo(100);
    }

    @Test
    public void deleteCartTest_successful() throws Exception {
        // given
        long cartId = 1L;
        long userId = 1L;
        Cart cart = makeCartFixture();

        // when
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // then
        cartService.deleteCart(cartId, userId);
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void calculateTotalPrice() {
        long userId = 1L;
        User user = User.builder().build();
        user.setId(userId);
        Product product1 = Product.builder().id(1L).price(10000).build();
        Product product2 = Product.builder().id(2L).price(20000).build();
        Product product3 = Product.builder().id(3L).price(30000).build();

        List<Cart> cartList = Arrays.asList( // 130_000원
                Cart.builder().user(user).product(product1).build(),
                Cart.builder().user(user).product(product2).build(),
                Cart.builder().user(user).product(product3).build()
        );

        when(cartRepository.findAllByUserId(userId)).thenReturn(cartList);
        int actualSum = cartList.stream()
                .map(Cart::getProduct)
                .map(Product::getPrice)
                .mapToInt(Integer::intValue)
                .sum();


        assertThat(actualSum).isEqualTo(60000);
    }

    private Cart makeCartFixture() {
        User user = User.builder().build();
        user.setId(1L);
        Product product = Product.builder().id(2L).build();

        Cart cart = new Cart(user, product, 1);
        cart.setId(1L);
        return cart;
    }
}