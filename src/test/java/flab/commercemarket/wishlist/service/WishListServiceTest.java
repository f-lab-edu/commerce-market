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
import flab.commercemarket.domain.wishlist.repository.WishListRepository;
import flab.commercemarket.domain.wishlist.vo.WishList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class WishListServiceTest {


    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private WishListRepository wishListRepository;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @InjectMocks
    private WishListService wishListService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void registerWishListTest_success() throws Exception {
        // given
        long userId = 1L;
        long productId = 2L;
        User user = User.builder().id(userId).build();
        Product product = Product.builder().id(productId).build();

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(userId)).thenReturn(product);
        WishList expectedWishList = WishList.builder().product(product).user(user).build();

        when(wishListRepository.save(any(WishList.class))).thenReturn(expectedWishList);
        WishList actualWishList = wishListService.registerWishList(userId, productId);

        // then
        assertNotNull(actualWishList);
        assertThat(actualWishList.getId()).isEqualTo(expectedWishList.getId());
        assertThat(actualWishList.getUserId()).isEqualTo(expectedWishList.getUserId());
        assertThat(actualWishList.getProductId()).isEqualTo(expectedWishList.getProductId());
    }

    @Test
    @DisplayName("찜목록 등록시 사용자가 없으면 DataNotFoundException 예외 발생")
    public void registerWishListTest_not_found_user() throws Exception {
        // given
        long userId = 1L;
        long productId = 2L;

        // when
        when(userService.getUserById(userId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.registerWishList(userId, productId));
    }

    @Test
    @DisplayName("찜목록 등록시 등록하는 상품이 없으면 DataNotFoundException 예외 발생")
    public void registerWishListTest_not_found_product() throws Exception {
        // given
        long userId = 1L;
        long productId = 2L;

        // when
        when(productService.getProduct(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.registerWishList(userId, productId));
    }

    @Test
    @DisplayName("현재 등록하는 상품이 해당 유저의 위시리스트에 이미 존재하는 상품이라면 예외를 발생시킨다.")
    public void registerWishListTest_DuplicateWishList() throws Exception {
        // given
        long userId = 1L;
        long duplicatedProductId = 2L;
        User user = User.builder().id(userId).build();
        Product product = Product.builder().id(duplicatedProductId).build();

        List<WishList> userWishLists = userWishListFixture(user);

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(userId)).thenReturn(product);
        when(wishListRepository.isExistProductInUserWishList(userId, duplicatedProductId)).thenReturn(userWishLists);
        // then

        assertThrows(DuplicateDataException.class, () -> wishListService.registerWishList(userId, duplicatedProductId));
    }

    @Test
    @DisplayName("WishList 목록 조회 성공 테스트")
    public void findWishListsTest() {
        // given
        long userId = 1L;
        User user = User.builder().id(userId).build();

        List<WishList> fakeWishLists = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            WishList wishList = new WishList();
            fakeWishLists.add(wishList);
        }

        when(userService.getUserById(userId)).thenReturn(user);

        // when
        PageRequest pageable = PageRequest.of(0, 10);
        Page<WishList> fakeWishListPage = new PageImpl<>(fakeWishLists, pageable, fakeWishLists.size());
        when(wishListRepository.findAllByUser(pageable, user)).thenReturn(fakeWishListPage);

        Page<WishList> result = wishListService.findWishLists(userId, 1, 10);

        // then
        assertEquals(fakeWishListPage, result);
        verify(userService, times(1)).getUserById(userId);
        verify(wishListRepository, times(1)).findAllByUser(pageable, user);
    }

    @Test
    @DisplayName("사용자의 찜목록 조회시, userId가 존재하지 않으면 예외가 발생한다.")
    public void findWishListsTest_not_found_user() throws Exception {
        // given
        long userId = 1L;
        int page = 2;
        int size = 10;

        // when
        when(userService.getUserById(userId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.findWishLists(userId, page, size));
    }

    @Test
    public void deleteWishListTest_success() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 2L;
        long productId = 3L;

        User user = User.builder().id(userId).build();
        Product product = Product.builder().id(productId).build();
        WishList wishList = new WishList(wishListId, user, product);

        when(wishListRepository.getWishListById(wishListId)).thenReturn(Optional.of(wishList));
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, wishList.getUserId());
        wishListService.deleteWishList(userId, wishListId);

        // when
        verify(authorizationHelper, times(1)).checkUserAuthorization(userId, wishList.getUserId());
        verify(wishListRepository, times(1)).delete(wishList);
    }

    @Test
    @DisplayName("위리리스트가 존재하지 않을때 DataNotFoundException 발생시킨다.")
    public void deleteWishListTest_WishListNotFound() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 2L;
        long productId = 3L;

        User user = User.builder().id(userId).build();
        Product product = Product.builder().id(productId).build();
        WishList wishList = new WishList(wishListId, user, product);

        // when
        when(wishListRepository.getWishListById(wishListId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.deleteWishList(userId, productId));
        verify(wishListRepository, never()).delete(wishList);
    }

    @Test
    @DisplayName("위리리스트의 유저 정보와 요청을 보낸 유저 정보가 일치하지 않으면 ForbiddenException 발생")
    public void deleteWishListTest_unauthorized() throws Exception {
        long userId = 123L;
        long wishListId = 456L;
        long productId = 100L;

        long differentUserId = 789L;

        User user = User.builder().id(userId).build();
        User differentUser = User.builder().id(differentUserId).build();
        Product product = Product.builder().id(productId).build();
        WishList wishList = new WishList(wishListId, user, product);

        when(wishListRepository.getWishListById(wishListId)).thenReturn(Optional.of(wishList));

        doThrow(new ForbiddenException("유저 권한정보가 일치하지 않음")).when(authorizationHelper)
                .checkUserAuthorization(user.getId(), differentUser.getId());

        assertThrows(ForbiddenException.class, () -> wishListService.deleteWishList(differentUserId, wishListId));
    }

    private List<WishList> userWishListFixture(User user) {
        return Arrays.asList(
                new WishList(1L, user, Product.builder().id(1L).build()),
                new WishList(2L, user, Product.builder().id(2L).build()),
                new WishList(3L, user, Product.builder().id(3L).build()),
                new WishList(4L, user, Product.builder().id(4L).build()),
                new WishList(5L, user, Product.builder().id(5L).build())
        );
    }

}
