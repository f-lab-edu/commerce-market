package flab.commercemarket.cart.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.cart.CartService;
import flab.commercemarket.domain.cart.mapper.CartMapper;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductService productService;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void notNullCheck() throws Exception {
        assertThat(cartService).isNotNull();
        assertThat(cartMapper).isNotNull();
        assertThat(productService).isNotNull();
    }

    @Test
    @DisplayName("장바구니 등록 성공")
    public void registerCartTest_successfulRegister() throws Exception {
        // given
        long productId = 1L;
        long userId = 1L;
        Cart data = makeCartFixture();

        // when
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, userId);
        when(cartMapper.isExistentProduct(productId)).thenReturn(true);
        when(cartMapper.isAlreadyExistentProductInUserCart(userId, productId)).thenReturn(false);

        Cart registerCart = cartService.registerCart(data, userId);

        // then
        assertThat(data.getUserId()).isEqualTo(registerCart.getUserId());
        assertThat(data.getProductId()).isEqualTo(registerCart.getProductId());
        assertThat(data.getQuantity()).isEqualTo(registerCart.getQuantity());
    }

    @Test
    @DisplayName("상품정보가 존재하지 않을때 예외를 발생시킨다.")
    public void registerCartTest_not_found_product() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        Cart data = makeCartFixture();

        // when
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, userId);
        when(cartMapper.isExistentProduct(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.registerCart(data, userId);
        });
    }

    @Test
    @DisplayName("장바구니에 이미 존재하는 상품을 다시 담으면 예외가 발생한다.")
    public void registerCartTest_duplicate_product() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        Cart data = makeCartFixture();

        // when
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, userId);
        when(cartMapper.isExistentProduct(productId)).thenReturn(true);
        when(cartMapper.isAlreadyExistentProductInUserCart(userId, productId)).thenThrow(DuplicateDataException.class);

        // then
        assertThrows(DuplicateDataException.class, () -> {
            cartService.registerCart(data, userId);
        });
    }

    @Test
    @DisplayName("업데이할 수량의 정보를 입력받아 정보를 수정해야한다.")
    void updateCartTest_successfulUpdate() {
        // given
        long cartId = 1L;
        long userId = 1L;
        long productId = 1L;

        Cart existCart = makeCartFixture();

        // when
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(existCart));
        Cart data = new Cart(userId, productId, 100);
        data.setId(cartId);

        Cart result = cartService.updateCart(data, cartId, userId);

        // then
        assertThat(existCart.getId()).isEqualTo(result.getId());
        assertThat(existCart.getUserId()).isEqualTo(result.getUserId());
        assertThat(existCart.getProductId()).isEqualTo(result.getProductId());
        assertThat(data.getQuantity()).isEqualTo(result.getQuantity());
    }

    @Test
    @DisplayName("user와 product의 정보는 업데이트되면 안되고 quantity만 업데이트 되어야한다.")
    public void updateCartTest_do_not_change_userAndProduct_info() throws Exception {
        // given
        long cartId = 1L;
        long userId = 1L;
        Cart existCart = makeCartFixture();

        // when
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(existCart));
        Cart data = new Cart(100L, 100L, 10);
        Cart result = cartService.updateCart(data, cartId, userId);

        // then
        assertThat(result.getUserId()).isNotEqualTo(data.getUserId());
        assertThat(result.getProductId()).isNotEqualTo(data.getProductId());
        assertThat(result.getUserId()).isEqualTo(existCart.getUserId());
        assertThat(result.getProductId()).isEqualTo(existCart.getUserId());
        assertThat(result.getQuantity()).isEqualTo(data.getQuantity());
    }

    @Test
    @DisplayName("장바구니 정보가 조회되지 않으면 예외를 발생시켜야한다.")
    public void updateCartTest_cartNotFount() throws Exception {
        // given
        long userId = 1L;
        long cartId = 1L;

        // when
        when(cartMapper.findById(cartId)).thenReturn(Optional.empty());
        Cart data = new Cart(100L, 100L, 10);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            cartService.updateCart(data, cartId, userId);
        });
    }

    @Test
    @DisplayName("인증된 user와 장바구니에 저정되어있는 user 정보가 다르면 예외를 발생시킨다.")
    public void updateCartTest_unauthorizedUser() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        long cartId = 1L;
        Cart existCart = new Cart(userId, productId, 1);
        existCart.setId(cartId);

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(existCart));

        long deniedUserId = 2L;
        Cart data = new Cart(deniedUserId, productId, 2);
        data.setId(cartId);

        doThrow(new ForbiddenException("유저 권한 정보가 일치하지 않음")).when(authorizationHelper).checkUserAuthorization(userId, deniedUserId);

        assertThrows(ForbiddenException.class, () -> {
            cartService.updateCart(data, cartId, deniedUserId);
        });
    }

    @Test
    @DisplayName("유저의 장바구니 목록을 전체 조회")
    void getCarts() {
        long userId = 1L;

        List<Cart> expectedCartList = Arrays.asList(
                new Cart(1L, 1L, 10),
                new Cart(1L, 2L, 20),
                new Cart(1L, 3L, 30)
        );

        when(cartService.findCarts(userId)).thenReturn(expectedCartList);
        List<Cart> result = cartService.findCarts(userId);

        assertThat(result.size()).isEqualTo(3);
        for (Cart cart : result) {
            assertThat(cart.getUserId()).isEqualTo(userId);
        }
    }

    @Test
    public void deleteCartTest_successful() throws Exception {
        // given
        long cartId = 1L;
        long userId = 1L;
        Cart cart = makeCartFixture();

        // when
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));

        // then
        cartService.deleteCart(cartId, userId);
        verify(cartMapper, times(1)).deleteCart(cartId);
    }

    @Test
    @DisplayName("유저 권한 정보가 일치하지 않으면 Forbidden 예외를 발생시킨다.")
    void deleteCartTest_forbidden() {
        long cartId = 1L;
        long userId = 1L;
        Cart existCart = makeCartFixture();

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(existCart));

        long deniedUserId = 2L; // 유저 권한 정보가 다름
        doThrow(ForbiddenException.class)
                .when(authorizationHelper).checkUserAuthorization(userId, deniedUserId);

        assertThrows(ForbiddenException.class, () -> {
            cartService.deleteCart(cartId, deniedUserId);
        });

        verify(cartMapper,times(0)).deleteCart(cartId);
    }

    @Test
    void calculateTotalPrice() {
        long userId = 1L;

        List<Cart> cartList = Arrays.asList( // 130_000원
                new Cart(1L, 1L, 2), // 10000원 x 2개
                new Cart(1L, 2L, 1), // 20000원 x 1개
                new Cart(1L, 3L, 3) // 30000원 x 3개
        );

        List<Product> productList = makeProductListFixture();

        when(cartService.findCarts(userId)).thenReturn(cartList);
        when(productService.getProduct(1L)).thenReturn(productList.get(0));
        when(productService.getProduct(2L)).thenReturn(productList.get(1));
        when(productService.getProduct(3L)).thenReturn(productList.get(2));

        int expectedSum = calculateExpectedSum(cartList, productList);
        int actualSum = cartService.calculateTotalPrice(userId);

        assertThat(actualSum).isEqualTo(expectedSum);
    }

    private int calculateExpectedSum(List<Cart> cartList, List<Product> productList) {
        int sum = 0;

        for (Cart cart : cartList) {
            int quantity = cart.getQuantity();
            int price = getPrice(cart.getProductId(), productList);
            sum += quantity * price;
        }
        return sum;
    }

    private int getPrice(long productId, List<Product> productList) {
        for (Product product : productList) {
            if (product.getId() == productId) {
                return product.getPrice();
            }
        }
        throw new DataNotFoundException("상품 정보를 찾을 수 없음 " + productId);
    }

    private Cart makeCartFixture() {
        Cart cart = new Cart(1L, 1L, 1);
        cart.setId(1L);
        return cart;
    }

    private List<Product> makeProductListFixture() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10000);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20000);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Product 3");
        product3.setPrice(30000);

        return Arrays.asList(product1, product2, product3);
    }
}