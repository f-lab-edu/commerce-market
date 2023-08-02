package flab.commercemarket.domain.wishlist;

import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.common.helper.WishlistHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.wishlist.mapper.WishListMapper;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListMapper wishListMapper;
    private final UserService userService;
    private final ProductService productService;
    private final AuthorizationHelper authorizationHelper;
    private final WishlistHelper wishlistHelper;

    @Transactional
    public void registerWishList(long userId, long productId) {
        log.info("Start registerWishList");

        userService.getUserById(userId);
        productService.findProduct(productId);
        wishlistHelper.verifyDuplicatedWishList(userId, productId);

        wishListMapper.insertWishList(userId, productId);
        log.info("userId = {}, productId = {}", userId, productId);
    }

    @Transactional(readOnly = true)
    public List<WishList> getWishLists(long userId, int page, int size) {
        log.info("Start getWishLists");

        int limit = size;
        int offset = (page - 1) * size;

        userService.getUserById(userId);
        log.info("Get WishList userId = {}", userId);
        return wishListMapper.getWishListItemByUserIdWithPagination(userId, limit, offset);
    }

    @Transactional(readOnly = true)
    public int countWishListByUserId(long userId) {
        log.info("Start getWishListCountByUserId = {}", userId);

        return wishListMapper.getWishListCountByUserId(userId);
    }

    @Transactional
    public void deleteWishList(long userId, long wishListId) {
        log.info("Start Delete WishList");

        WishList wishList = wishlistHelper.verifyWishList(wishListId);
        authorizationHelper.checkUserAuthorization(wishList.getUserId(), userId);

        wishListMapper.deleteWishList(wishListId);
        log.info("Delete WishList = {}", wishListId);
    }

}
