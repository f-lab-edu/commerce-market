package flab.commercemarket.product.like;

import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Like implements LikeStrategy {
    private final ProductMapper productMapper;

    @Override
    public void updateLikeCount(Product product) {
        int currentLikeCount = product.getLikeCount();
        int newLikeCount = currentLikeCount + 1;
        product.setLikeCount(newLikeCount);
        productMapper.updateLikeCount(product);

        log.info("New LikeCount = {}", newLikeCount);
    }
}
