package flab.commercemarket.common.helper;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.wishlist.mapper.WishListMapper;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WishlistHelper {

    private final WishListMapper wishListMapper;

    public void verifyDuplicatedWishList(long userId, long productId) {
        List<WishList> userWishLists = wishListMapper.getWishListItemByUserId(userId);
        boolean isDuplicate = userWishLists.stream()
                .map(WishList::getProductId)
                .anyMatch(wishListProductId -> wishListProductId == productId);

        if (isDuplicate) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 위시리스트에 존재하는 상품입니다.");
        }
    }

    public WishList verifyWishList(long wishListId) {
        Optional<WishList> optionalWishList = wishListMapper.findById(wishListId);
        return optionalWishList.orElseThrow(() -> {
            log.warn("wishListId = {}", wishListId);
            return new DataNotFoundException("조회한 위시리스트가 없음");
        });
    }
}
