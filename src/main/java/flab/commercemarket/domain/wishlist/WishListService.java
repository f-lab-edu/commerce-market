package flab.commercemarket.domain.wishlist;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.wishlist.repository.WishListRepository;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final AuthorizationHelper authorizationHelper;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public WishList registerWishList(long userId, long productId) {
        log.info("Start registerWishList");

        User user = userService.getUserById(userId);
        Product product = productService.getProduct(productId);

        checkDuplicatedProductInUserWishList(userId, productId);

        WishList wishList = WishList.builder()
                .product(product)
                .user(user)
                .build();

        WishList savedWishList = wishListRepository.save(wishList);
        log.info("userId = {}, productId = {}", userId, productId);
        return savedWishList;
    }

    @Transactional(readOnly = true)
    public Page<WishList> findWishLists(long userId, int page, int size) {

        log.info("Get WishList userId = {}", userId);
        User foundUser = userService.getUserById(userId);

        Pageable pageable = PageRequest.of(page - 1, size);
        return wishListRepository.findAllByUser(pageable, foundUser);
    }

    @Transactional
    public void deleteWishList(long userId, long wishListId) {
        log.info("Start Delete WishList");

        WishList wishList = getWishList(wishListId);
        authorizationHelper.checkUserAuthorization(wishList.getUserId(), userId);

        wishListRepository.delete(wishList);
        log.info("Delete WishList = {}", wishListId);
    }

    private WishList getWishList(long wishListId) {
        log.info("Start Get WishList. wishListId: {}", wishListId);
        Optional<WishList> optionalWishList = wishListRepository.getWishListById(wishListId);
        return optionalWishList.orElseThrow(() -> {
            log.warn("wishListId = {}", wishListId);
            return new DataNotFoundException("조회한 위시리스트가 없음");
        });
    }

    private void checkDuplicatedProductInUserWishList(long userId, long productId) {
        List<WishList> foundUserWishLists = wishListRepository.isExistProductInUserWishList(userId, productId);
        boolean result = foundUserWishLists.stream()
                .anyMatch(wishList -> wishList.getUserId() == userId && wishList.getProductId() == productId);

        if (result) {
            throw new DuplicateDataException("사용자의 WishList에 이미 존재하는 상품");
        }
    }
}
