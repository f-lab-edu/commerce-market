package flab.commercemarket.product.like;

import flab.commercemarket.product.domain.Product;

public interface LikeStrategy {

    void updateLikeCount(Product product);
}
