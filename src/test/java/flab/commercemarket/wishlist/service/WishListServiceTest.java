package flab.commercemarket.wishlist.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.common.helper.WishlistHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.wishlist.WishListService;
import flab.commercemarket.domain.wishlist.mapper.WishListMapper;
import flab.commercemarket.domain.wishlist.vo.WishList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class WishListServiceTest {

    @Mock
    private WishListMapper wishListMapper;

    @Mock
    private UserService userService;

    @Mock
    ProductService productService;

    @Mock
    AuthorizationHelper authorizationHelper;

    @Mock
    WishlistHelper wishlistHelper;

    @InjectMocks
    private WishListService wishListService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void notNullCheck() throws Exception {
        assertThat(wishListMapper).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(productService).isNotNull();
        assertThat(authorizationHelper).isNotNull();
    }

    @Test
    public void registerWishListTest_success() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        User user = userFixture(userId);
        Product product = productFixture(productId);

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.findProduct(productId)).thenReturn(product);
        wishListService.registerWishList(userId, productId);

        // then
        verify(userService, times(1)).getUserById(userId);
        verify(productService, times(1)).findProduct(productId);
    }

    @Test
    @DisplayName("현재 등록하는 상품이 해당 유저의 위시리스트에 이미 존재하는 상품이라면 예외를 발생시킨다.")
    public void registerWishListTest_DuplicateWishList() throws Exception {
        // given
        long userId = 1L;
        long productId = 1L;
        User user = userFixture(userId);
        Product product = productFixture(productId);
        List<WishList> userWishLists = wishListsFixture();

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.findProduct(productId)).thenReturn(product);
        when(wishListMapper.getWishListItemByUserId(1L)).thenReturn(userWishLists);
        doThrow(DuplicateDataException.class).when(wishlistHelper).verifyDuplicatedWishList(userId, productId);

        assertThrows(DuplicateDataException.class, () -> wishListService.registerWishList(userId, productId));
    }

    @Test
    public void getWishListTest() throws Exception {
        // given
        long userId = 1L;
        int page = 2;
        int size = 10;

        // when
        List<WishList> wishLists = new ArrayList<>();
        when(userService.getUserById(userId)).thenReturn(new User());
        when(wishListMapper.getWishListItemByUserIdWithPagination(userId, size, page - 1)).thenReturn(wishLists);

        // then
        List<WishList> result = wishListService.getWishLists(userId, page, size);
        assertThat(wishLists).isEqualTo(result);
    }

    @Test
    @DisplayName("WishList 조회 페이지네이션 적용")
    public void getWishListTest_pagination() throws Exception {
        // given
        long userId = 1L;
        int page = 1;
        int size = 10;

        List<WishList> wishLists = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            WishList wishList = new WishList((long) i, i, userId);
            wishLists.add(wishList);
        }

        // when
        when(userService.getUserById(userId)).thenReturn(new User());
        when(wishListMapper.getWishListItemByUserIdWithPagination(userId, size, page - 1)).thenReturn(wishLists.subList(0, size));

        List<WishList> result = wishListService.getWishLists(userId, page, size);

        // then
        assertThat(result).isEqualTo(wishLists.subList(0, size));
    }

    @Test
    public void getWishListCountByUserIdTest() throws Exception {
        // given
        long userId = 1L;
        int count = 5;

        // when
        when(wishListMapper.getWishListCountByUserId(userId)).thenReturn(count);
        int result = wishListService.getWishListCountByUserId(userId);

        // then
        assertThat(count).isEqualTo(result);
    }

    @Test
    public void deleteWishListTest_success() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 1L;
        long productId = 1L;

        WishList wishList = new WishList(wishListId, productId, userId);

        when(wishlistHelper.verifyWishList(wishListId)).thenReturn(wishList);

        doNothing().when(authorizationHelper).checkUserAuthorization(userId, wishList.getUserId());

        // when
        wishListService.deleteWishList(userId, wishListId);

        verify(wishlistHelper, times(1)).verifyWishList(wishListId);
        verify(authorizationHelper, times(1)).checkUserAuthorization(userId, wishList.getUserId());
        verify(wishListMapper, times(1)).deleteWishList(wishListId);
    }

    @Test
    @DisplayName("위리리스트가 존재하지 않을때 DataNotFoundException 발생시킨다.")
    public void deleteWishListTest_WishListNotFound() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 1L;

        // when
        when(wishListMapper.findById(wishListId)).thenReturn(Optional.empty());
        doThrow(DataNotFoundException.class).when(wishlistHelper).verifyWishList(wishListId);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.deleteWishList(userId, wishListId));

        verify(wishlistHelper, times(1)).verifyWishList(wishListId);
        verify(wishListMapper, never()).deleteWishList(wishListId);

    }

    @Test
    @DisplayName("위리리스트의 유저 정보와 요청을 보낸 유저 정보가 일치하지 않으면 ForbiddenException 발생")
    public void deleteWishListTest_unauthorized() throws Exception {
        long userId = 123L;
        long wishListId = 456L;
        long productId = 100L;

        long differentUserId = 789L;

        WishList wishList = new WishList(wishListId, productId, userId);
        when(wishlistHelper.verifyWishList(wishListId)).thenReturn(wishList);

        doThrow(new ForbiddenException("유저 권한정보가 일치하지 않음")).when(authorizationHelper)
                .checkUserAuthorization(userId, differentUserId);

        assertThrows(ForbiddenException.class, () -> wishListService.deleteWishList(differentUserId, wishListId));
    }

    private User userFixture(long userId) {
        return new User(userId, "username", "password", "name", "address", "01012345678", "abc@gmail.com");
    }

    private Product productFixture(long productId) {
        return new Product(productId, "productName", 1000, "url", "description", 100, 1, 0, 0, 1);
    }

    private List<WishList> wishListsFixture() {
        List<WishList> userWishLists = new ArrayList<>();
        userWishLists.add(new WishList(1L, 1L, 1L));
        userWishLists.add(new WishList(2L, 2L, 1L));
        userWishLists.add(new WishList(3L, 3L, 1L));
        return userWishLists;
    }
}
