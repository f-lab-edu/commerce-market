package flab.commercemarket.domain.wishlist;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.wishlist.mapper.WishListMapper;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListMapper wishListMapper;
    private final UserService userService;
    private final ProductService productService;
    private final AuthorizationHelper authorizationHelper;


    @Transactional
    public void registerWishList(long userId, long productId) {
        log.info("Start registerWishList");

        userService.getUserById(userId);
        productService.findProduct(productId);
        verifyDuplicatedWishList(userId, productId);

        wishListMapper.insertWishList(userId, productId);
        log.info("userId = {}, productId = {}", userId, productId);
    }

    @Transactional(readOnly = true)
    public List<WishList> getWishLists(long userId, int page, int size) {
        log.info("Start registerWishList");

        int limit = size;
        int offset = (page - 1) * size;

        userService.getUserById(userId);
        log.info("Get WishList userId = {}", userId);
        return wishListMapper.getWishListItemByUserIdWithPagination(userId, limit, offset);
    }

    @Transactional(readOnly = true)
    public int getWishListCountByUserId(long userId) {
        log.info("Start getWishListCountByUserId = {}", userId);

        return wishListMapper.getWishListCountByUserId(userId);
    }

    @Transactional
    public void deleteWishList(long userId, long wishListId) {
        log.info("Start Delete WishList");

        WishList wishList = verifyWishList(wishListId);
        authorizationHelper.checkUserAuthorization(wishList.getUserId(), userId);

        wishListMapper.deleteWishList(wishListId);
        log.info("Delete WishList = {}", wishListId);
    }

    private void verifyDuplicatedWishList(long userId, long productId) {
        List<WishList> userWishLists = wishListMapper.getWishListItemByUserId(userId);
        boolean isDuplicate = userWishLists.stream()
                .map(WishList::getProductId)
                .anyMatch(wishListProductId -> wishListProductId == productId);

        if (isDuplicate) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 위시리스트에 존재하는 상품입니다.");
        }
    }

    private WishList verifyWishList(long wishListId) {
        Optional<WishList> optionalWishList = wishListMapper.findById(wishListId);
        return optionalWishList.orElseThrow(() -> {
            log.warn("wishListId = {}", wishListId);
            return new DataNotFoundException("조회한 위시리스트가 없음");
        });
    }
}
