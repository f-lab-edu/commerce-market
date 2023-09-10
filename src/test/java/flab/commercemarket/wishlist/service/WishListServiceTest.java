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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class WishListServiceTest {

    @Mock
    private WishListRepository wishListRepository;

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
    public void registerWishListTest_success() throws Exception {
        // given
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(productId)).thenReturn(product);
        WishList wishList = WishList.builder().product(product).user(user).build();
        when(wishListRepository.save(any())).thenReturn(wishList);

        WishList expectedWishlist = wishListService.registerWishList(userId, productId);

        // then
        assertThat(wishList.getUserId()).isEqualTo(expectedWishlist.getUserId());

    }

    @Test
    @DisplayName("찜목록 등록시 사용자가 없으면 DataNotFoundException 예외 발생")
    public void registerWishListTest_not_found_user() throws Exception {
        // given
        long productId = 1L;
        long userId = 1L;

        // when
        when(userService.getUserById(userId)).thenThrow(DataNotFoundException.class);

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
        when(productService.getProduct(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> wishListService.registerWishList(userId, productId));
    }

    @Test
    @DisplayName("현재 등록하는 상품이 해당 유저의 위시리스트에 이미 존재하는 상품이라면 예외를 발생시킨다.")
    public void registerWishListTest_DuplicateWishList() throws Exception {
        // given
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        WishList wishList = WishList.builder().user(user).product(product).build();
        List<WishList> userWishLists = Arrays.asList(wishList);

        // when
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProduct(productId)).thenReturn(product);
        when(wishListRepository.getWishListItemByUserId(userId)).thenReturn(userWishLists);
        when(wishListRepository.findById(userId)).thenReturn(Optional.of(new WishList()));

        DuplicateDataException exception = assertThrows(DuplicateDataException.class,
                () -> wishListService.registerWishList(userId, productId));

        assertEquals("이미 위시리스트에 존재하는 상품입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("찜목록을 정상적으로 조회하는 경우")
    public void findWishListsTest() throws Exception {
        long userId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        // 모의 객체를 설정하여 원하는 결과를 반환하도록 설정합니다.
        List<WishList> expectedWishLists = wishListFixture(); // 원하는 결과 데이터 생성
        when(wishListRepository.findAllByUserId(userId, pageable)).thenReturn(expectedWishLists);

        // Act
        List<WishList> actualWishLists = wishListService.findWishLists(userId, page, size);

        // Assert
        assertEquals(expectedWishLists.size(), actualWishLists.size());
    }

    @Test
    public void getWishListCountByUserIdTest() throws Exception {
        // given
        long userId = 1L;
        long count = 5;

        // when
        when(wishListRepository.countByUserId(userId)).thenReturn(count);
        long actualCount = wishListService.countWishListByUserId(userId);

        // then
        assertThat(count).isEqualTo(actualCount);
    }

    @Test
    public void deleteWishListTest_success() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 1L;
        long productId = 1L;

        User user = new User();
        user.setId(userId);
        Product product = new Product();
        product.setId(productId);
        WishList wishList = WishList.builder().user(user).product(product).build();

        when(wishListRepository.findById(wishListId)).thenReturn(Optional.ofNullable(wishList));
        doNothing().when(authorizationHelper).checkUserAuthorization(userId, wishList.getUserId());

        // when
        wishListService.deleteWishList(userId, wishListId);

        verify(authorizationHelper, times(1)).checkUserAuthorization(userId, wishList.getUserId());
        verify(wishListRepository, times(1)).delete(wishList);
    }

    @Test
    @DisplayName("위리리스트가 존재하지 않을때 DataNotFoundException 발생시킨다.")
    public void deleteWishListTest_WishListNotFound() throws Exception {
        // given
        long wishListId = 1L;
        long userId = 1L;

        // when
        when(wishListRepository.findById(wishListId)).thenReturn(Optional.empty()); // Mocking a non-existent wishlist

        assertThrows(DataNotFoundException.class,
                () -> wishListService.deleteWishList(userId, wishListId));

        verify(wishListRepository, never()).delete(new WishList());
    }

    @Test
    @DisplayName("위리리스트의 유저 정보와 요청을 보낸 유저 정보가 일치하지 않으면 ForbiddenException 발생")
    public void deleteWishListTest_unauthorized() throws Exception {
        long userId = 123L;
        long wishListId = 456L;
        long productId = 100L;

        long differentUserId = 789L;
        Product product = new Product();
        product.setId(productId);

        User user = new User();
        user.setId(userId);

        WishList wishList = WishList.builder()
                .id(wishListId)
                .product(product)
                .user(user)
                .build();

        when(wishListRepository.findById(wishListId)).thenReturn(Optional.of(wishList));

        doThrow(new ForbiddenException("유저 권한정보가 일치하지 않음")).when(authorizationHelper)
                .checkUserAuthorization(userId, differentUserId);

        assertThrows(ForbiddenException.class, () -> wishListService.deleteWishList(differentUserId, wishListId));
    }

    private List<WishList> wishListFixture() {
        List<WishList> wishLists = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        Product product1 = new Product();
        product1.setId(1L);

        User user2 = new User();
        user1.setId(2L);
        Product product2 = new Product();
        product1.setId(2L);

        wishLists.add(new WishList(1L, product1, user1));
        wishLists.add(new WishList(2L, product2, user2));
        return wishLists;
    }
}
