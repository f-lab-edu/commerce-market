package flab.commercemarket.domain.product;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.mapper.ProductMapper;
import flab.commercemarket.domain.product.vo.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final AuthorizationHelper authorizationHelper;
    private final ProductMapper productMapper;

    public Product registerProduct(Product product) {
        log.info("Start registerProduct");

        // todo 인증로직 구현 후 product에 sellerID를 묶어주어야함.
        productMapper.insertProduct(product);

        log.info("Create Product. {}", product);
        return product;
    }

    public Product updateProduct(long productId, Product data) {
        log.info("Start updateProduct");

        Product foundProduct = getProduct(productId);

        authorizationHelper.checkUserAuthorization(foundProduct.getSellerId(), data.getSellerId());

        foundProduct.setName(data.getName());
        foundProduct.setPrice(data.getPrice());
        foundProduct.setImageUrl(data.getImageUrl());
        foundProduct.setDescription(data.getDescription());
        foundProduct.setStockAmount(data.getStockAmount());

        productMapper.updateProduct(foundProduct);

        log.info("Update Product. productId = {}", productId);
        return foundProduct;
    }

    public Product getProduct(long productId) {
        log.info("Start get ProductId: {}", productId);

        Optional<Product> optionalProduct = productMapper.findById(productId);
        return optionalProduct.orElseThrow(() -> {
            log.info("productId = {}", productId);
            return new DataNotFoundException("조회한 상품 정보가 없음");
        });
    }

    public List<Product> findProducts(int page, int size) {
        log.info("Find All Product. page = {}, size = {}", page, size);
        int limit = size;
        int offset = (page - 1) * size;

        return productMapper.findAll(offset, limit);
    }

    public int countProducts() {
        log.info("Start getProductCount");

        return productMapper.countProduct();
    }

    public List<Product> searchProduct(String keyword, int page, int size) {
        log.info("Start searchProduct with keyword. keyword = {}", keyword);

        int limit = size;
        int offset = (page -1) * size;

        return productMapper.searchProduct(keyword, offset, limit);
    }

    public int countSearchProductByKeyword(String keyword) {
        log.info("Start countSearchProductByKeyword. keyword = {}", keyword);

        return productMapper.searchProductCountByKeyword(keyword);
    }

    public void deleteProduct(long productId, long loginUserId) {
        Product foundProduct = getProduct(productId);

        authorizationHelper.checkUserAuthorization(foundProduct.getSellerId(), loginUserId);

        productMapper.deleteProduct(foundProduct.getId());
        log.info("Delete Product. ProductId = {}", productId);
    }

    public void updateLikeCount(long productId) {
        log.info("Start increaseLikeCount");
        Product product = getProduct(productId);
        // todo 로그인된 유저가 상품을 구매했는지 검증하는 로직이 필요함

        int likeCount = product.getLikeCount();
        int newLikeCount = likeCount + 1;

        product.setLikeCount(newLikeCount);
        productMapper.updateLikeCount(product);

        log.info("New LikeCount = {}", newLikeCount);
    }
}
