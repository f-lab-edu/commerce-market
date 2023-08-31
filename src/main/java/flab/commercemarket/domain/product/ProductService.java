package flab.commercemarket.domain.product;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.repository.ProductRepository;
import flab.commercemarket.domain.product.vo.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final AuthorizationHelper authorizationHelper;
    private final ProductRepository productRepository;

    @Transactional
    public Product registerProduct(Product product) {
        log.info("Start registerProduct");

        productRepository.save(product);

        log.info("Create Product. {}", product);
        return product;
    }

    @Transactional
    public Product updateProduct(long productId, Product data) {
        log.info("Start updateProduct");

        Product foundProduct = getProduct(productId);

        authorizationHelper.checkUserAuthorization(foundProduct.getSellerId(), data.getSellerId());

        foundProduct.setName(data.getName());
        foundProduct.setPrice(data.getPrice());
        foundProduct.setImageUrl(data.getImageUrl());
        foundProduct.setDescription(data.getDescription());
        foundProduct.setStockAmount(data.getStockAmount());

        productRepository.save(foundProduct);

        log.info("Update Product. productId = {}", productId);
        return foundProduct;
    }

    @Transactional(readOnly = true)
    public Product getProduct(long productId) {
        log.info("Start get ProductId: {}", productId);

        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElseThrow(() -> {
            log.info("productId = {}", productId);
            return new DataNotFoundException("조회한 상품 정보가 없음");
        });
    }

    @Transactional(readOnly = true)
    public Page<Product> findProducts(int page, int size) {
        log.info("Find All Product. page = {}, size = {}", page, size);

        Pageable pageable = PageRequest.of(page - 1, size);

        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProduct(String keyword, int page, int size) {
        log.info("Start searchProduct with keyword. keyword = {}", keyword);

        Pageable pageable = PageRequest.of(page - 1, size);

        return productRepository.findByKeyword(pageable, keyword);
    }

    @Transactional
    public void deleteProduct(long productId, long loginUserId) {
        Product foundProduct = getProduct(productId);

        authorizationHelper.checkUserAuthorization(foundProduct.getSellerId(), loginUserId);
        productRepository.delete(foundProduct);
        log.info("Delete Product. ProductId = {}", productId);
    }

    @Transactional
    public void updateLikeCount(long productId) {
        log.info("Start increaseLikeCount");
        Product product = getProduct(productId);
        // todo 로그인된 유저가 상품을 구매했는지 검증하는 로직이 필요함

        int likeCount = product.getLikeCount();
        int newLikeCount = likeCount + 1;

        productRepository.updateLikeCount(productId, newLikeCount);

        log.info("New LikeCount = {}", newLikeCount);
    }
}
