package flab.commercemarket.domain.wishlist;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.wishlist.repository.WishListRepository;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public WishList registerWishList(long userId, long productId) {
        log.info("Start registerWishList");

        User foundUser = userService.getUserById(userId);
        Product foundProduct = productService.getProductById(productId);
        verifyDuplicatedWishList(userId, productId);

        WishList wishList = WishList.builder()
                .user(foundUser)
                .product(foundProduct)
                .build();

        WishList savedWishList = wishListRepository.save(wishList);
        log.info("userId = {}, productId = {}", userId, productId);
        return savedWishList;
    }

    @Transactional(readOnly = true)
    public List<WishList> findWishLists(long userId, int page, int size) {
        log.info("Start registerWishList");

        Pageable pageable = PageRequest.of(page - 1, size);

        log.info("Get WishList userId = {}", userId);
        return wishListRepository.findAllByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countWishListByUserId(long userId) {
        log.info("Start getWishListCountByUserId = {}", userId);

        return wishListRepository.countByUserId(userId);
    }

    @Transactional
    public void deleteWishList(long userId, long wishListId) {
        log.info("Start Delete WishList");

        WishList foundWishList = getWishList(wishListId);
        checkAuthorization(userId, foundWishList);

        wishListRepository.delete(foundWishList);
        log.info("Delete WishList = {}", wishListId);
    }

    private WishList getWishList(long wishListId) {
        Optional<WishList> optionalWishList = wishListRepository.findById(wishListId);
        return optionalWishList.orElseThrow(() -> {
            log.info("getWishList. wishListId: {}", wishListId);
            return new DataNotFoundException("조회한 찜목록 정보가 없음");
        });
    }

    private void verifyDuplicatedWishList(long userId, long productId) {
        List<WishList> userWishLists = wishListRepository.getWishListItemByUserId(userId);
        boolean isDuplicate = userWishLists.stream()
                .map(WishList::getProduct)
                .map(Product::getId)
                .anyMatch(wishListProductId -> wishListProductId == productId);

        if (isDuplicate) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 위시리스트에 존재하는 상품입니다.");
        }
    }

    private void checkAuthorization(long userId, WishList foundWishList) {
        if (userId != foundWishList.getUserId()) {
            throw new ForbiddenException("권한 정보 일치하지 않음");
        }
    }
}
