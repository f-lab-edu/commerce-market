package flab.commercemarket.wishlist.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
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
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(wishListMapper.isExistentUser(userId)).thenReturn(true);
        when(wishListMapper.isExistentProduct(productId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(productId)).thenReturn(product);

        // then
        wishListService.registerWishList(userId, productId);

        verify(wishListMapper, Mockito.times(1)).insertWishList(userId, productId);
    }

    @Test
    @DisplayName("찜목록 등록시 사용자가 없으면 DataNotFoundException 예외 발생")
    public void registerWishListTest_not_found_user() throws Exception {
        // given
        long productId = 1L;
        long userId = 1L;

        // when
        when(wishListMapper.isExistentUser(userId)).thenReturn(false);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.registerWishList(userId, productId));
    }

    @Test
    @DisplayName("찜목록 등록시 등록하는 상품이 없으면 DataNotFoundException 예외 발생")
    public void registerWishListTest_not_found_product() throws Exception {
        // given
        long productId = 1L;
        long userId = 1L;

        // when
        when(wishListMapper.isExistentProduct(productId)).thenReturn(false);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.registerWishList(userId, productId));
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
        when(wishListMapper.isExistentUser(userId)).thenReturn(true);
        when(wishListMapper.isExistentProduct(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(productId)).thenReturn(product);
        when(wishListMapper.getWishListItemByUserId(userId)).thenReturn(userWishLists);
        when(wishListMapper.findById(userId)).thenReturn(Optional.of(new WishList())); // Mocking an existing duplicate item

        DuplicateDataException exception = assertThrows(DuplicateDataException.class,
                () -> wishListService.registerWishList(userId, productId));

        assertEquals("이미 위시리스트에 존재하는 상품입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("찜목록을 정상적으로 조회하는 경우")
    public void findWishListsTest() throws Exception {
        // given
        long userId = 1L;
        int page = 2;
        int size = 10;

        // when
        List<WishList> wishLists = new ArrayList<>();
        when(wishListMapper.isExistentUser(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(new User());
        when(wishListMapper.getWishListItemByUserIdWithPagination(userId, size, page - 1)).thenReturn(wishLists);

        // then
        List<WishList> result = wishListService.getWishLists(userId, page, size);
        assertThat(wishLists).isEqualTo(result);
    }

    @Test
    @DisplayName("사용자의 찜목록 조회시, userId가 존재하지 않으면 예외가 발생한다.")
    public void findWishListsTest_not_found_user() throws Exception {
        // given
        long userId = 1L;
        int page = 2;
        int size = 10;

        // when
        when(wishListMapper.isExistentUser(userId)).thenReturn(false);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.findWishLists(userId, page, size));
    }

    @Test
    @DisplayName("WishList 조회 페이지네이션 적용")
    public void findWishListsTest_pagination() throws Exception {
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
        when(wishListMapper.isExistentUser(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(new User());
        when(wishListMapper.getWishListItemByUserIdWithPagination(userId, size, page - 1)).thenReturn(wishLists.subList(0, size));

        List<WishList> result = wishListService.getWishLists(userId, page, size);

        // then
        assertThat(result).isEqualTo(wishLists.subList(0, size));
    }

    @Test
    public void findWishLists_not_found_user() throws Exception {
        // given
        long userId = 100L;
        int page = 1;
        int size = 10;

        // when
        when(wishListMapper.isExistentUser(userId)).thenReturn(false);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.getWishLists(userId, page, size));
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

        when(wishListMapper.findById(wishListId)).thenReturn(Optional.of(wishList));
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, wishList.getUserId());

        // when
        wishListService.deleteWishList(userId, wishListId);

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
        when(wishListMapper.findById(wishListId)).thenReturn(Optional.empty()); // Mocking a non-existent wishlist

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> wishListService.deleteWishList(userId, wishListId));

        assertEquals("조회한 위시리스트가 없음", exception.getMessage());

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
        when(wishListMapper.findById(wishListId)).thenReturn(Optional.of(wishList));


        doThrow(new ForbiddenException("유저 권한정보가 일치하지 않음")).when(authorizationHelper)
                .checkUserAuthorization(userId, differentUserId);

        assertThrows(ForbiddenException.class, () -> wishListService.deleteWishList(differentUserId, wishListId));
    }

    private User userFixture(long userId) {
        return new User(userId, "username", "password", "name", "address", "01012345678", "abc@gmail.com");
    }

    private Product productFixture(long productId) {
        return new Product(productId, "productName", 1000, "url", "description", 100, 1, 0, 0);
    }

    private List<WishList> wishListsFixture() {
        List<WishList> userWishLists = new ArrayList<>();
        userWishLists.add(new WishList(1L, 1L, 1L));
        userWishLists.add(new WishList(2L, 2L, 1L));
        userWishLists.add(new WishList(3L, 3L, 1L));
        return userWishLists;
    }
}
