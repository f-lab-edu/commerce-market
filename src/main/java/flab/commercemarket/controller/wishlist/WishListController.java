package flab.commercemarket.controller.wishlist;

import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.wishlist.dto.WishListResponseDto;
import flab.commercemarket.domain.wishlist.WishListService;
import flab.commercemarket.domain.wishlist.vo.WishList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wishes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
public class WishListController {

    private final WishListService wishListService;
    private final AuthorizationHelper authorizationHelper;

    @PostMapping("/{productId}")
    public WishListResponseDto postWishList(@PathVariable long productId) {
        // TODO @RequestParam long userId 제거 -> 로그인 정보에서 userId 가져오도록 refactoring
        String principalEmail = authorizationHelper.getPrincipalEmail();
        WishList wishList = wishListService.registerWishList(principalEmail, productId);
        return wishList.toWishlistResponseDto();
    }

    @GetMapping
    public PageResponseDto<WishListResponseDto> getWishLists(@RequestParam int page, @RequestParam int size) {
        String principalEmail = authorizationHelper.getPrincipalEmail();
        List<WishList> wishLists = wishListService.findWishLists(principalEmail, page, size);
        long totalElements = wishListService.countWishListByUserId(principalEmail);

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
    public void deleteWishList(@PathVariable long wishListId) {
        String principalEmail = authorizationHelper.getPrincipalEmail();
        wishListService.deleteWishList(principalEmail, wishListId);
    }
}