package flab.commercemarket.product.like;

import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DisLike implements LikeStrategy {

    private final ProductMapper productMapper;


    @Override
    public void updateLikeCount(Product product) {
        int currentDislikeCount = product.getDislikeCount();
        int newDislikeCount = currentDislikeCount + 1;
        product.setDislikeCount(newDislikeCount);
        productMapper.updateDislikeCount(product);
        log.info("New DislikeCount = {}", newDislikeCount);
    }
}
