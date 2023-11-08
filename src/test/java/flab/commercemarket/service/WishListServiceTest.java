package flab.commercemarket.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.wishlist.WishListService;
import flab.commercemarket.domain.wishlist.repository.WishListRepository;
import flab.commercemarket.domain.wishlist.vo.WishList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishListServiceTest {
    @Mock
    WishListRepository wishListRepository;

    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @InjectMocks
    WishListService wishListService;

    long userId = 1L;
    long productId = 1L;
    User user;
    Product product;

    @BeforeEach
    void init() {
        user = User.builder().id(userId).role(Role.USER).email("a@gmail.com").build();
        product = Product.builder().id(productId).name("productName").build();
    }

    @Test
    @DisplayName("찜 목록을 등록한다.")
    public void registerWishListTest() throws Exception {
        // given
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProductById(productId)).thenReturn(product);
        when(wishListRepository.getWishListItemByUserId(userId)).thenReturn(new ArrayList<>());

        WishList wishList = WishList.builder().user(user).product(product).build();
        when(wishListRepository.save(any(WishList.class))).thenReturn(wishList);

        // when
        WishList result = wishListService.registerWishList(userId, productId);

        // then
        assertThat(userId).isEqualTo(result.getUserId());
        assertThat(productId).isEqualTo(result.getProduct().getId());
    }

    @Test
    @DisplayName("찜 목록 등록 시 사용자가 존재하지 않으면 예외가 발생한다.")
    public void registerWishListTest_notFountUser() throws Exception {
        // given
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProductById(userId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            wishListService.registerWishList(userId, productId);
        });
    }

    @Test
    @DisplayName("찜 목록 등록 시 상품이 존재하지 않으면 예외가 발생한다.")
    public void registerWishListTest_notFountProduct() throws Exception {
        // given
        when(userService.getUserById(userId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            wishListService.registerWishList(userId, productId);
        });
    }

    @Test
    @DisplayName("userId로 사용자 찜 목록을 조회한다.")
    public void findWishListsTest() {
        // Given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<WishList> wishLists = new ArrayList<>();
        when(wishListRepository.findAllByUserId(userId, pageable)).thenReturn(wishLists);

        // When
        List<WishList> result = wishListService.findWishLists(userId, page, size);

        // Then
        verify(wishListRepository).findAllByUserId(userId, pageable);
        assertThat(result).isEqualTo(wishLists);
    }

    @Test
    public void countWishListByUserIdTest() throws Exception {
        // given
        List<WishList> wishLists = wishListsFixture();
        when(wishListRepository.countByUserId(userId)).thenReturn((long) wishLists.size());

        // when
        long result = wishListService.countWishListByUserId(userId);

        // then
        assertThat(wishLists.size()).isEqualTo(result);
    }

    @Test
    public void deleteWishListTest() throws Exception {
        // given
        long wishListId = 10L;
        WishList wishList = wishListFixture(wishListId);
        when(wishListRepository.findById(wishListId)).thenReturn(Optional.of(wishList));

        // when
        wishListService.deleteWishList(userId, wishListId);

        // then
        verify(wishListRepository, times(1)).delete(wishList);
    }

    @Test
    public void deleteWishListTest_Forbidden_Exception() throws Exception {
        // given
        long unauthorizedUserId = 100;
        long wishListId = 10L;
        WishList wishList = wishListFixture(wishListId);
        when(wishListRepository.findById(wishListId)).thenReturn(Optional.of(wishList));

        // then
        assertThrows(ForbiddenException.class, () ->
                wishListService.deleteWishList(unauthorizedUserId, wishListId));
    }

    private WishList wishListFixture(long wishlistId) {
        return WishList.builder().id(wishlistId).user(user).product(product).build();
    }

    private List<WishList> wishListsFixture() {
        return IntStream.range(1,10)
                .mapToObj(this::wishListFixture)
                .collect(Collectors.toList());
    }
}
