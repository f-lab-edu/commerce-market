package flab.commercemarket.domain.product;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.ForbiddenException;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

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

        checkUserAuthorization(foundProduct.getSellerId(), data.getSellerId());

        foundProduct.setName(data.getName());
        foundProduct.setPrice(data.getPrice());
        foundProduct.setImageUrl(data.getImageUrl());
        foundProduct.setDescription(data.getDescription());
        foundProduct.setStockAmount(data.getStockAmount());

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
    public List<Product> searchProduct(String keyword, int page, int size) {
        log.info("Start searchProduct with keyword. keyword = {}", keyword);

        Pageable pageable = PageRequest.of(page - 1, size);

        return productRepository.findByKeyword(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public long countSearchProductByKeyword(String keyword) {
        log.info("Start countSearchProductByKeyword. keyword = {}", keyword);
        return productRepository.countSearchProductByKeyword(keyword);
    }

    @Transactional
    public void deleteProduct(long productId, long loginUserId) {
        Product foundProduct = getProduct(productId);

        checkUserAuthorization(foundProduct.getSellerId(), loginUserId);
        productRepository.delete(foundProduct);
        log.info("Delete Product. ProductId = {}", productId);
    }

    @Transactional
    public void updateLikeCount(long productId) {
        log.info("Start increaseLikeCount");
        Product foundProduct = getProduct(productId);

        int likeCount = foundProduct.getLikeCount();
        int newLikeCount = likeCount + 1;

        foundProduct.setLikeCount(newLikeCount);
    }

    private void checkUserAuthorization(long ownerUserId, long loginUserId) {
        if (ownerUserId != loginUserId) {
            log.info("dataUserId = {}, loginUserId = {}", ownerUserId, loginUserId);
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }
    }
}
