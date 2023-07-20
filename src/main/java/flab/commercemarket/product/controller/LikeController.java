package flab.commercemarket.product.controller;

import flab.commercemarket.product.dto.LikeDto;
import flab.commercemarket.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final ProductService productService;

    @PostMapping("/likes")
    public void postLike(@RequestBody LikeDto likeDto) {
        productService.updateLikeCount(likeDto.getProductId());
    }
}

