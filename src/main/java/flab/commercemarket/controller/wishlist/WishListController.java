package flab.commercemarket.controller.wishlist;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import flab.commercemarket.domain.wishlist.WishListService;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wishes")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/{productId}")
    public void postWishList(@PathVariable long productId, @RequestParam long userId) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        wishListService.registerWishList(userId, productId);
    }

    @GetMapping
    public PageResponseDto<WishListResponseDto> getWishLists(@RequestParam long userId,
                                        @RequestParam int page,
                                        @RequestParam int size) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        List<WishList> wishLists = wishListService.findWishLists(userId, page, size);
        int totalElements = wishListService.findWishListCountByUserId(userId);

        List<WishListResponseDto> wishListResponseList = wishLists.stream()
                .map(WishList::toWishlistResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<WishListResponseDto>builder()
                .size(size)
                .page(page)
                .totalElements(totalElements)
                .content(wishListResponseList)
                .build();
    }

    @DeleteMapping("/{wishListId}")
    public void deleteWishList(@PathVariable long wishListId, @RequestParam long userId) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        wishListService.deleteWishList(userId, wishListId);
    }
}