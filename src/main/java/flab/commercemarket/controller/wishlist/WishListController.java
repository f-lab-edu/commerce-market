package flab.commercemarket.controller.wishlist;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import flab.commercemarket.domain.wishlist.WishListService;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wishes")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/{productId}")
    public WishListResponseDto postWishList(@PathVariable long productId, @RequestParam long userId) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        WishList wishList = wishListService.registerWishList(userId, productId);
        return wishList.toWishlistResponseDto();
    }

    @GetMapping
    public PageResponseDto<WishListResponseDto> getWishLists(@RequestParam long userId,
                                        @RequestParam int page,
                                        @RequestParam int size) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        Page<WishList> wishListPage = wishListService.findWishLists(userId, page, size);

        List<WishListResponseDto> wishListResponseList = wishListPage.stream()
                .map(WishList::toWishlistResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<WishListResponseDto>builder()
                .size(size)
                .page(page)
                .totalElements(wishListPage.getTotalElements())
                .content(wishListResponseList)
                .build();
    }

    @DeleteMapping("/{wishListId}")
    public void deleteWishList(@PathVariable long wishListId, @RequestParam long userId) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        wishListService.deleteWishList(userId, wishListId);
    }
}