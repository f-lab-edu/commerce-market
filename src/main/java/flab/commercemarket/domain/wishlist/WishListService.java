package flab.commercemarket.domain.wishlist;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
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
    public WishList registerWishList(String email, long productId) {
        log.info("Start registerWishList");
        User foundUser = userService.getUserByEmail(email);

        Product foundProduct = productService.getProduct(productId);
        verifyDuplicatedWishList(foundUser.getId(), productId);

        WishList wishList = WishList.builder()
                .user(foundUser)
                .product(foundProduct)
                .build();

        WishList savedWishList = wishListRepository.save(wishList);
        log.info("user's email = {}, productId = {}", email, productId);
        return savedWishList;
    }

    @Transactional(readOnly = true)
    public List<WishList> findWishLists(String email, int page, int size) {
        log.info("Start findWishLists");
        Pageable pageable = PageRequest.of(page - 1, size);
        User foundUser = userService.getUserByEmail(email);
        return wishListRepository.findAllByUserId(foundUser.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public long countWishListByUserId(String email) {
        log.info("Start getWishListCountByUserId");
        User foundUser = userService.getUserByEmail(email);
        return wishListRepository.countByUserId(foundUser.getId());
    }

    @Transactional
    public void deleteWishList(String email, long wishListId) {
        log.info("Start Delete WishList");
        WishList foundWishList = getWishList(wishListId);
        User foundUser = userService.getUserByEmail(email);

        checkUserAuthorization(foundWishList.getUserId(), foundUser.getId());

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

    private void checkUserAuthorization(long ownerUserId, long loginUserId) {
        if (ownerUserId != loginUserId) {
            log.info("dataUserId = {}, loginUserId = {}", ownerUserId, loginUserId);
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }
    }
}
